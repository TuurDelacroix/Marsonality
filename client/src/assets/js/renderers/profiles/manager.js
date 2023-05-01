'use strict';

async function showProfileManager(profile)
{
    if (managerNotHidden)
    {
        toggleManagerVisibility();
        toggleCarrouselVisibility();
    }

    const defaultTraits = await get(`traits/${getCurrentMarsId()}`)
        .then(res => res.json())
        .then(traits => {return traits;});

    const $baseTraitList = document.querySelector(".base>ul.trait-list");
    insertBaseTraits(defaultTraits, $baseTraitList);

    const $additionalTraitList = document.querySelector(".additional>ul.trait-list");
    insertAdditionalTraits(profile, $additionalTraitList);

    const ownedTraits = await get(`martians/${getCurrentMarsId()}/traits`)
        .then(res => res.json())
        .then(traits => {return traits;});
    const $ownedTraitList = document.querySelector(".owned-traits>ul.trait-list");

    const filteredOwnedTraits = filterOwnedTraits(ownedTraits, profile.traits);
    insertOwnedTraits(filteredOwnedTraits, $ownedTraitList);

    const $activateButton = document.querySelector("#activate-profile");
    $activateButton.addEventListener('click', () => activateProfile(profile));

    setBasicProfileDetails(profile);

    initDragAndDrop(profile);
}

function setBasicProfileDetails(profile) {
    setProfilePicture(profile);
    setProfileManagerName(profile);
    setTraitTotal(profile);
}

function activateProfile(profile)
{
    try {
        put(`martians/${getCurrentMarsId()}/profiles/${profile.name}/activate`)
            .then(displayPopupAlert("ACTIVATION", `Profile "${profile.name}" has been activated.`, "Ok")
                .then(() => goBackCarrousel(profile)));
    } catch(err)
    {
        console.error(err);
    }
}

function filterOwnedTraits(ownedTraits, traitsOnProfile)
{
    traitsOnProfile.forEach(profileTrait => {
        ownedTraits.splice(ownedTraits.findIndex(trait => trait.name === profileTrait.name), 1);
    });
    return ownedTraits;
}

function insertBaseTraits(defaultTraits, $htmlTarget)
{
    $htmlTarget.innerHTML = "";
    defaultTraits.forEach(trait => {
        $htmlTarget.insertAdjacentHTML("beforeend", `<li class="trait-name">${trait.name}</li>`);
    });
}

function insertOwnedTraits(ownedTraits, $htmlTarget)
{
    $htmlTarget.innerHTML = "";
    ownedTraits.forEach(trait => {
        $htmlTarget.insertAdjacentHTML("beforeend", `<li id="${trait.name}" class="trait-name draggable-trait" draggable="true">${trait.name}</li>`);
    });
}

function insertAdditionalTraits(profile, $htmlTarget)
{

    $htmlTarget.innerHTML = "";
    const possibleTraits = profile.number;
    const profileTraits = profile.traits;

    for (let traitNr = 0; traitNr < possibleTraits; traitNr++) {
        if (traitNr < profileTraits.length)
        {
            const html = `<li id="${profileTraits[traitNr].name}" class="trait-name draggable-trait" draggable="true">${profileTraits[traitNr].name}</li>`;
            $htmlTarget.insertAdjacentHTML("beforeend", html);
        }
        else
        {
            $htmlTarget.insertAdjacentHTML("beforeend", `<li class="trait-name">Empty Slot</li>`);
        }
    }

}

function toggleManagerVisibility()
{
    document.querySelector(".profile-manager").toggleAttribute("hidden");
}

function toggleCarrouselVisibility()
{
    document.querySelector(".profiles-main").toggleAttribute("hidden");
}

function managerNotHidden()
{
    return document.querySelector(".profile-manager").getAttribute("hidden") == null;
}

function setProfilePicture(profile)
{
    const $profileImage = document.querySelector(".profile-image>img");

    if (profile.inUse)
    {
        $profileImage.classList.add("active");
    }
    else {
        $profileImage.classList.remove("active");
    }
    $profileImage.setAttribute("src", `https://avatars.dicebear.com/api/avataaars/${(profile.name).replace(" ", "%20")}.svg`);
}

function setProfileManagerName(profile)
{
    const $profileNameTitle = document.querySelector(".profile-image-title");
    $profileNameTitle.textContent = profile.name;
}

function setTraitTotal(profile)
{
    const currentTraitTotal = profile.traits.length;

    let possibleTraits;
    if (profile.name.includes("DEFAULT")) {
        possibleTraits = profile.traits.size;
    }
    else {
        possibleTraits = profile.number;
    }

    const $traitTotal = document.querySelector("#trait-total");
    $traitTotal.textContent = `${currentTraitTotal}/${possibleTraits}`;
}

function goBackCarrousel(profile)
{
    initBreadCrumbs( ".profiles-main", "");
    if (profile === null)
    {
        loadCarrouselData();
        toggleCarrouselVisibility();
        toggleManagerVisibility();
    }
    else
    {
        if (checkIfDefaultProfile(profile))
        {
            loadCarrouselData();
        }
        else {
            loadCarrouselData();
            toggleCarrouselVisibility();
            toggleManagerVisibility();
        }
    }
}
