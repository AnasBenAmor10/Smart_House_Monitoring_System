const cacheName = "Phoenix-Fleet-Manager-V1";
const assets = [
  "/",
  "/index.html",
  "/pages/home.html",
  "/pages/countries.html",
  "/scripts/events.js",
  "/scripts/util.js",
  "/scripts/iam.js",
  "/scripts/mvp.js",
  "/scripts/router.js",
  "/scripts/main.js",
  "/scripts/home.js",
  "/scripts/countries.js",  
  "/images/icons/icon-48x48.png",
  "/images/icons/icon-72x72.png",
  "/images/icons/icon-96x96.png",
  "/images/icons/icon-128x128.png",
  "/images/icons/icon-144x144.png",
  "/images/icons/icon-152x152.png",
  "/images/icons/icon-192x192.png",
  "/images/icons/icon-384x384.png",
  "/images/icons/icon-512x512.png"
];

caches.keys().then(function(names) {
    for (let name of names)
        caches.delete(name);
});

self.addEventListener("install", installEvent => {
  installEvent.waitUntil(
    caches.open(cacheName).then(cache => {
      cache.addAll(assets);
    })
  );
});

self.addEventListener("fetch", fetchEvent => {
  fetchEvent.respondWith(
    caches.match(fetchEvent.request).then(res => {
      return res || fetch(fetchEvent.request);
    })
  );
});