import VanillaRouter from "./router.js";
function loadPageScript(page) {
    const existingScript = document.querySelector(`script[data-page="${page}"]`);
    if (existingScript) {
        console.log(`Script for page ${page} is already loaded.`);
        return;
    }
    const oldScripts = document.querySelectorAll('script[data-page]');
    oldScripts.forEach(script => script.remove());

    const scriptUrl = `scripts/${page}.js`;
    fetch(scriptUrl, { method: "HEAD" })
        .then(response => {
            if (response.ok) {
                const script = document.createElement("script");
                script.src = scriptUrl;
                script.type = "module";
                script.defer = true;
                script.dataset.page = page;
                document.body.appendChild(script);

                console.log(`Script for page ${page} loaded.`);
            }
        })
}



const router = new VanillaRouter({
    type: "history",
    routes: {
        "/": "Home",
        "/Register": "SignUp",
        "/Login": "SignIn"
    }
})
    .listen()
    .on("route", async (e) => {
        let mainElement = document.getElementById("mainContent");
        const route = e.detail.route.replace("/", "");

        try {
            const htmlPath = `pages/${route}.html`;
            let text = await fetch(htmlPath)
                .then((response) => {
                    if (!response.ok) {
                        throw new Error(`HTTP error! status: ${response.status}`);
                    }
                    return response.text();
                });
            mainElement.innerHTML = text;
            loadPageScript(route);
        } catch (err) {
            console.error(`Failed to load page ${route}:`, err);
            mainElement.innerHTML = `<h1>Erreur 404</h1><p>La page demandée est introuvable.</p>`;
        }
    });
