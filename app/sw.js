const cacheName = "smarthome-v1"; 
const assets = [
  "/",
  "/scripts/events.js",
  "/scripts/iam.js",
  "/scripts/router.js",
  "/scripts/main.js", 
  "/scripts/home.js",
  "/scripts/mvp.js",
  "/pages/dashboard.html",
  "/styles/main.css",
  "/images/icons/android-chrome-192x192.png",
  "/images/icons/android-chrome-512x512.png",
  "/images/icons/apple-touch-icon.png",
  "/images/icons/favicon-16x16.png",
  "/images/icons/favicon-32x32.png",
  "/images/back1.png",
  "/images/logo.png",
  "/images/mobile.png",
  "/images/profile.png"
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