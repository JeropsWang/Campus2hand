const http = require('http');
const fs = require('fs');
const path = require('path');

const PORT = 5173;
const DIST_DIR = path.join(__dirname, 'dist');
const ROOT_DIR = __dirname;

const mimeTypes = {
  '.html': 'text/html',
  '.js': 'application/javascript',
  '.css': 'text/css',
  '.json': 'application/json',
  '.png': 'image/png',
  '.jpg': 'image/jpeg',
  '.svg': 'image/svg+xml',
  '.ico': 'image/x-icon',
  '.woff': 'font/woff',
  '.woff2': 'font/woff2',
  '.map': 'application/json'
};

function setCorsHeaders(res) {
  res.setHeader('Access-Control-Allow-Origin', '*');
  res.setHeader('Access-Control-Allow-Methods', 'GET, POST, PUT, DELETE, OPTIONS');
  res.setHeader('Access-Control-Allow-Headers', 'Content-Type, Authorization, X-Requested-With');
  res.setHeader('Access-Control-Allow-Credentials', 'true');
}

const server = http.createServer((req, res) => {
  console.log(`${req.method} ${req.url}`);

  if (req.method === 'OPTIONS') {
    setCorsHeaders(res);
    res.writeHead(204);
    res.end();
    return;
  }

  if (req.url.startsWith('/api/')) {
    proxyApi(req, res);
    return;
  }

  let filePath = path.join(DIST_DIR, req.url);

  if (req.url === '/' || !path.extname(req.url)) {
    filePath = path.join(DIST_DIR, 'index.html');
  }

  const extname = path.extname(filePath);
  const contentType = mimeTypes[extname] || 'text/html';

  fs.readFile(filePath, (error, content) => {
    if (error) {
      if (error.code === 'ENOENT') {
        fs.readFile(path.join(DIST_DIR, 'index.html'), (err, indexContent) => {
          if (err) {
            res.writeHead(404, { 'Content-Type': 'text/html' });
            res.end('<h1>404 Not Found</h1>', 'utf-8');
          } else {
            setCorsHeaders(res);
            res.writeHead(200, { 'Content-Type': 'text/html' });
            res.end(indexContent, 'utf-8');
          }
        });
      } else {
        res.writeHead(500);
        res.end('Server Error: ' + error.code, 'utf-8');
      }
    } else {
      setCorsHeaders(res);
      res.writeHead(200, { 'Content-Type': contentType });
      res.end(content, 'utf-8');
    }
  });
});

function proxyApi(req, res) {
  const apiPath = req.url;
  const apiUrl = 'http://localhost:8080' + apiPath;

  const url = new URL(apiUrl);

  const options = {
    hostname: url.hostname,
    port: url.port || 8080,
    path: url.pathname + url.search,
    method: req.method,
    headers: {
      'Content-Type': 'application/json',
      ...req.headers
    }
  };

  const proxyReq = http.request(options, (proxyRes) => {
    setCorsHeaders(res);
    res.writeHead(proxyRes.statusCode, proxyRes.headers);
    proxyRes.pipe(res);
  });

  proxyReq.on('error', (e) => {
    console.error('Proxy error:', e);
    setCorsHeaders(res);
    res.writeHead(500);
    res.end('Proxy Error: ' + e.message);
  });

  req.pipe(proxyReq);
}

server.listen(PORT, () => {
  console.log(`Production server running at http://localhost:${PORT}/`);
  console.log(`Serving files from: ${DIST_DIR}`);
  console.log('CORS enabled for all origins');
});