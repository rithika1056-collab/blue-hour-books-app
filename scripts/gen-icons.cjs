const fs = require('fs');
const zlib = require('zlib');
const path = require('path');

function crc32(buf) {
  let c = ~0;
  for (let i = 0; i < buf.length; i++) {
    c ^= buf[i];
    for (let k = 0; k < 8; k++) c = (c >>> 1) ^ (0xedb88320 & -(c & 1));
  }
  return ~c >>> 0;
}

function chunk(type, data) {
  const len = Buffer.alloc(4);
  len.writeUInt32BE(data.length, 0);
  const typeBuf = Buffer.from(type, 'ascii');
  const crc = Buffer.alloc(4);
  crc.writeUInt32BE(crc32(Buffer.concat([typeBuf, data])), 0);
  return Buffer.concat([len, typeBuf, data, crc]);
}

function makePng(width, height, rgba) {
  const sig = Buffer.from([137, 80, 78, 71, 13, 10, 26, 10]);
  const ihdr = Buffer.alloc(13);
  ihdr.writeUInt32BE(width, 0);
  ihdr.writeUInt32BE(height, 4);
  ihdr[8] = 8; // bit depth
  ihdr[9] = 6; // color type RGBA
  ihdr[10] = 0; ihdr[11] = 0; ihdr[12] = 0;
  const raw = Buffer.alloc((width * 4 + 1) * height);
  let o = 0;
  for (let y = 0; y < height; y++) {
    raw[o++] = 0; // filter none
    for (let x = 0; x < width; x++) {
      const i = (y * width + x) * 4;
      raw[o++] = rgba[i];
      raw[o++] = rgba[i + 1];
      raw[o++] = rgba[i + 2];
      raw[o++] = rgba[i + 3];
    }
  }
  const idat = zlib.deflateSync(raw);
  return Buffer.concat([sig, chunk('IHDR', ihdr), chunk('IDAT', idat), chunk('IEND', Buffer.alloc(0))]);
}

function lerp(a, b, t) { return a + (b - a) * t; }

function generate(size) {
  const rgba = Buffer.alloc(size * size * 4);
  const cx = size / 2;
  const cy = size * 0.42;
  const radius = size * 0.22;
  const corner = size * 0.22;

  for (let y = 0; y < size; y++) {
    for (let x = 0; x < size; x++) {
      const i = (y * size + x) * 4;
      // rounded rect mask
      const rx = x, ry = y;
      const dx = Math.max(corner - rx, rx - (size - corner), 0);
      const dy = Math.max(corner - ry, ry - (size - corner), 0);
      const inCorner = (dx * dx + dy * dy) <= corner * corner;
      const inRect = rx >= 0 && ry >= 0 && rx < size && ry < size && (dx === 0 || dy === 0 || inCorner);
      if (!inRect) { rgba[i+3] = 0; continue; }

      // background gradient (radial from top)
      const dist = Math.sqrt((x - cx) * (x - cx) + (y - cy) * (y - cy)) / (size * 0.75);
      const t = Math.min(1, dist);
      const r = Math.round(lerp(35, 12, t));
      const g = Math.round(lerp(50, 18, t));
      const b = Math.round(lerp(96, 36, t));
      rgba[i] = r; rgba[i+1] = g; rgba[i+2] = b; rgba[i+3] = 255;

      // moon (gold circle, top right)
      const moonCx = size * 0.74, moonCy = size * 0.26, moonR = size * 0.085;
      const md = Math.sqrt((x - moonCx) ** 2 + (y - moonCy) ** 2);
      if (md < moonR) {
        rgba[i] = 245; rgba[i+1] = 196; rgba[i+2] = 81;
      } else if (md < moonR + size * 0.012) {
        rgba[i] = 230; rgba[i+1] = 168; rgba[i+2] = 35;
      }

      // three book spines (center, rotated feel via simple vertical bars)
      const bookCenterX = size * 0.5;
      const bookTop = size * 0.32;
      const bookBottom = size * 0.74;
      const spineWidth = size * 0.085;
      const spines = [
        { offset: -size * 0.13, color: [152, 132, 251] },
        { offset: 0, color: [230, 168, 35] },
        { offset: size * 0.13, color: [84, 112, 179] },
      ];
      for (const s of spines) {
        const sx = bookCenterX + s.offset;
        if (x >= sx - spineWidth / 2 && x <= sx + spineWidth / 2 && y >= bookTop && y <= bookBottom) {
          rgba[i] = s.color[0]; rgba[i+1] = s.color[1]; rgba[i+2] = s.color[2];
        }
      }
    }
  }
  return makePng(size, size, rgba);
}

const outDir = path.join(__dirname, '..', 'public');
fs.writeFileSync(path.join(outDir, 'icon-192.png'), generate(192));
fs.writeFileSync(path.join(outDir, 'icon-512.png'), generate(512));
console.log('icons generated');
