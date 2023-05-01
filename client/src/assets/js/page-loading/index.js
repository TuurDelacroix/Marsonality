'use strict';

document.addEventListener("DOMContentLoaded", loadConfig);

function init()
{
    const $startButton = document.querySelector("#start-button");
    checkIfAlreadyCustomer($startButton);
    clickStart($startButton);
}

function checkIfAlreadyCustomer($startbutton) {
    const $marsMap = document.querySelector("#marsMap");

    if (getCurrentMartian() === null) {
        $startbutton.toggleAttribute("hidden");
        $marsMap.toggleAttribute("hidden");
        displayPopupAlert("OOPS", "You are not chipped yet! " +
            "To make use of our services we want you to navigate to our company and let your chip been planted. " +
            "We hope we see you soon!", "On My Way")
            .then(() => {
                loadMap();
            });

    }
    else {
        $startbutton.removeAttribute("hidden");
        $marsMap.toggleAttribute("hidden");

        document.querySelector("#user").toggleAttribute("hidden");
        document.querySelector("#building").toggleAttribute("hidden");
    }
}

function clickStart($startButton) {
    $startButton.addEventListener("click", () => {
        $startButton.innerHTML = `<em class="fas fa-spinner fa-spin"></em>`;
        setCurrentMartian(1);
        setTimeout(() => redirect('dashboard'), 2000);
    });

    requestPermission();
}

function requestPermission() {
	Notification.requestPermission().then((result) => {
		if(result === 'granted'){
            if ('serviceWorker' in navigator) {
                navigator.serviceWorker.register('sw.js');
                subscribeToNotifications();
            }
        }
	});
}
