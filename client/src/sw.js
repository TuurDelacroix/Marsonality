// this file must be in the ROOT of the project otherwise it will not work!!!!

self.addEventListener('push', (event) => {//NOSONAR
	const title = event.data.json().title;
	const options = event.data.json().options;
	self.registration.showNotification(title, options);
});

self.addEventListener('notificationclick', event => { //NOSONAR
    clients.openWindow("https://project-ii.ti.howest.be/mars-14/index.html");
    event.notification.close();
});
