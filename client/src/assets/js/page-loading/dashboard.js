'use strict';
document.addEventListener("DOMContentLoaded", loadConfig);

function init()
{
    listenToSideMenuActivity();
    listenToTraitDisplay();
    loadTableData();
    loadCarrouselData();
    listenToCarrouselActivity();
    getBoxInfoData();
    loadAllKindOfOwnedTraits();
    loadAllKindOfShopTraits();
    initCharts();
    initFullScreen();
    loadNotifications();
}

function listenToTraitDisplay()
{
    const collapsibleMenus = document.querySelectorAll(".collapsible");
    collapsibleMenus.forEach(collapsible => {
        collapsible.addEventListener("click", showCollapsibleInfo);
    });

    const $backButton = document.querySelector(".back-to-overview");
    $backButton.addEventListener('click', () => goBackCarrousel(null));
}

function showCollapsibleInfo(e)
{
    e.preventDefault();

    const currentTarget = e.currentTarget;

    this.classList.toggle("active");

    const content = this.nextElementSibling;
    if (content.style.display === "block") {
        toggleExpandIcon(currentTarget);
        content.style.display = "none";
    } else {
        toggleExpandIcon(currentTarget);
        content.style.display = "block";
    }
}

function toggleExpandIcon(iTarget)
{
    iTarget.querySelector("em").classList.toggle(`fa-plus`);
    iTarget.querySelector("em").classList.toggle(`fa-minus`);
}
