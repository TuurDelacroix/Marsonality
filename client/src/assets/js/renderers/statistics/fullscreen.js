"use strict";
// Using functions of fullscreen exercise from Web Technology

function initFullScreen() {
    document.querySelectorAll('.graph-display').forEach(chartContainer => {
        addFullscreenIcons(chartContainer);

        chartContainer.addEventListener("fullscreenchange", ev => {
            ev.currentTarget.querySelectorAll('.material-icons').forEach(icon => icon.classList.toggle('hidden'));
        });
    });
}

function addFullscreenIcons(chartContainer) {
    const fullscreen = document.createElement("div");
    fullscreen.classList.add('material-icons');
    fullscreen.innerText = "fullscreen";
    fullscreen.addEventListener("click", fullscreenToggle);

    const fullscreenExit = document.createElement("div");
    fullscreenExit.classList.add('material-icons');
    fullscreenExit.classList.add('hidden');
    fullscreenExit.innerText = "fullscreen_exit";
    fullscreenExit.addEventListener("click", fullscreenToggle);

    chartContainer.appendChild(fullscreen);
    chartContainer.appendChild(fullscreenExit);
}

function fullscreenToggle(event) {
    const target = event.currentTarget;
    const parent = target.parentElement;

    if (target.innerText === "fullscreen") {
        parent.requestFullscreen();
    } else {
        document.exitFullscreen();
    }
}

