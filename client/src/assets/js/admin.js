let api;

document.addEventListener('DOMContentLoaded', loadConfig);

function loadConfig() {
	fetch('config.json')
		.then((resp) => resp.json())
		.then((config) => {
			api = `${config.host ? config.host + '/' : ''}${
				config.group ? config.group + '/' : ''
			}api/`;
			init();
		});
}

function init() {
	document.getElementById('send').addEventListener('click', function () {
		const notification = {
			notification: {
				title: document.getElementById('title').value,
				options: {
					body: document.getElementById('body').value,
					icon: document.getElementById('icon').value,
					icon_url: document.getElementById('icon').value,
					badge_url: document.getElementById('icon').value,
					image_url: document.getElementById('icon').value,
					timestamp: Date.now(),
				},
			},
		};

		console.log(notification);

		post('notification', notification);
	});
}
