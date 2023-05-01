function subscribeToNotifications() {
	const options = {
		userVisibleOnly: true,
		applicationServerKey: urlBase64ToUint8Array(getPushPublicKey()),
	};

	navigator.serviceWorker.ready
		.then((registration) => {
			return registration.pushManager.subscribe(options);
		})
		.then((subscription) => {
			const key = subscription.getKey ? subscription.getKey('p256dh') : '';
			const auth = subscription.getKey ? subscription.getKey('auth') : '';

			post('subscribe', {
				endpoint: subscription.endpoint,
				key: btoa(String.fromCharCode.apply(null, new Uint8Array(key))),
				auth: btoa(String.fromCharCode.apply(null, new Uint8Array(auth))),
			});
		})
		.catch((err) => {
			console.error('Error subscribing to notifications', err);
		});
}

function urlBase64ToUint8Array(base64String) {
	const padding = '='.repeat((4 - (base64String.length % 4)) % 4);
	const base64 = (base64String + padding)
		.replace(/\-/g, '+')
		.replace(/_/g, '/');

	const rawData = window.atob(base64);
	const outputArray = new Uint8Array(rawData.length);

	for (let i = 0; i < rawData.length; ++i) {
		outputArray[i] = rawData.charCodeAt(i);
	}
	return outputArray;
}
