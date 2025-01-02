const cacheName = "Phoenix-Fleet-Manager-V1";
const assets = [
  "/",
  "/scripts/events.js",
  "/scripts/iam.js",
  "/scripts/router.js",
  "/scripts/main.js", 
  "/pages/home.html",
  "pages/dashboard.html",
  "styles/main.css"
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