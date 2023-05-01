'use strict';
// Popups
function displayPopupConfirm(title, text, accept, deny) {
    const $dialog = document.querySelector('#confirm-popup');
    $dialog.querySelector('.title').textContent = title;
    $dialog.querySelector('p').textContent = text;
    $dialog.querySelector('.accept').textContent = accept;
    $dialog.querySelector('.deny').textContent = deny;

    $dialog.showModal();

    return returnAPromise($dialog);
}

function displayPopupAlert(title, text, confirm) {
    const $dialog = document.querySelector('#alert-popup');
    $dialog.querySelector('h2').textContent = title;
    $dialog.querySelector('p').textContent = text;
    $dialog.querySelector('.accept').textContent = confirm;
    if (typeof $dialog.showModal === "function") {
        $dialog.showModal();
    }
    return returnAPromise($dialog);
}

function displayInputAlert(title, question, text, confirm)
{
    const $dialog = document.querySelector('#input-popup');
    $dialog.querySelector('h2').textContent = title;
    $dialog.querySelector('label[for="inputField"]').textContent = question;
    $dialog.querySelector('p#description').textContent = text;
    $dialog.querySelector('.accept').textContent = confirm;
    if (typeof $dialog.showModal === "function") {
        $dialog.showModal();
    }
    return returnAUnlockPromise($dialog);
}

function returnAPromise($dialog)
{
    return new Promise((resolve) => {
        $dialog.addEventListener('close', function onClose() {
            return resolve({action: $dialog.returnValue});
        });
    });
}

function returnAUnlockPromise($dialog)
{
    return new Promise((resolve) => {
        const errorMessage = $dialog.querySelector("#errormessage");
        let newProfileName = $dialog.querySelector('input').value;

        $dialog.querySelector('input').addEventListener("input", () => {
            newProfileName = $dialog.querySelector('input').value;
        });

        $dialog.addEventListener('submit', function onClose(e) {
            if (newProfileName === "")
            {

                errorMessage.textContent = "Profile name must be entered";
                errorMessage.toggleAttribute("hidden");
                e.preventDefault();
            }
            else
            {
                return resolve({action: $dialog.returnValue, value: newProfileName});
            }

            return null;
        });
    });
}
