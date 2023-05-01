'use strict';

function listenToCarrouselActivity()
{
    const profileContainer = document.querySelector(".profiles-carrousel");
    const nextButton = document.querySelector("em.c-button.r-button");
    const previousButton = document.querySelector("em.c-button.l-button");

    setTimeout(setUnlockCardEvent, 1000);


    nextButton.addEventListener('click', () => {
        profileContainer.scrollLeft += 250;
    });

    previousButton.addEventListener('click', () => {
        profileContainer.scrollLeft -= 250;
    });

}

function setUnlockCardEvent()
{
    const unlockProfileCard = document.querySelector(".profile-card.locked");
    unlockProfileCard.addEventListener('click', unlockProfile);
}
