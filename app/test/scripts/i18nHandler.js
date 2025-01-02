
// identify an element to observe
mainContent = window.document.getElementById('mainContent');

// create a new instance of 'MutationObserver' named 'observer', 
// passing it a callback function
observer = new MutationObserver((mutationsList, observer) => {
	let elems = document.querySelectorAll('[data-i18n]');
	elems.forEach((el) => { 
		const key = el.getAttribute('data-i18n');
		let placeholders = [];
		try{
			placeholders = JSON.parse(el.getAttribute('data-i18n-placeholders'));
			if (!Array.isArray(placeholders)) {
				placeholders = [];
			} 
		}catch(e){}
		el.innerText = ((placeholders.length === 0)?browser.i18n.getMessage(key):browser.i18n.getMessage(key,placeholders));
	});
});

// call 'observe' on that MutationObserver instance, 
// passing it the element to observe, and the options object
observer.observe(mainContent, {characterData: false, childList: true, subtree: true, attributes: false});