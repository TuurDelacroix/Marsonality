function loadNotifications() {
		get(`notifications`)
			.then((res) => res.json())
			.then((notifications) => {
				if (notifications.failure === 404) {
					noNotifications();
				} else {
					clearNotifications();
					notifications.forEach((notification) => {
                        if(!notification.options.timestamp) {
                            notification.options.timestamp = Date.now();
                        }
                        loadNotification(notification.title, notification.options.body, notification.options.icon, notification.options.timestamp);
					});
				}
			});
}

function noNotifications() {
	const $noNotifications = document.querySelector('#no-notifications');
	$noNotifications.removeAttribute('hidden');
}

function loadNotification(title, body, icon, timestamp) {
	const $notifications = document.querySelector('.notifications-main');
	const $notificationList = $notifications.querySelector('ul');
	const $notificationTemplate = document
		.querySelector('#notification-template')
		.content.cloneNode(true);

	const date = new Date(timestamp);

	$notificationTemplate.querySelector('.timestamp').textContent =
		date.toLocaleString();
	$notificationTemplate.querySelector('h3').textContent = title;
	$notificationTemplate.querySelector('h3+p').textContent = body;
	$notificationTemplate.querySelector('img').src = icon;

	$notificationList.insertBefore(
		$notificationTemplate,
		$notificationList.firstChild
	);
}

function clearNotifications() {
	const $noNotifications = document.querySelector('#no-notifications');
	const $notifications = document.querySelector('.notifications-main');
	const $notificationList = $notifications.querySelector('ul');

	$noNotifications.hidden = true;

	$notificationList.innerHTML = '';
}
