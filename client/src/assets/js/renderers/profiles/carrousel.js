'use strict';

function loadCarrouselData()
{
    getProfilesData()
        .then(() => {
            const loadedProfiles = document.querySelectorAll(".profile-card:not(.locked)");
            loadedProfiles.forEach(profileCard => {
                profileCard.addEventListener("click", (e) => {
                    e.preventDefault();
                    const clickedProfile =  e.target.closest(".profile-card");
                    const profileName = clickedProfile.querySelector(".profile-title").textContent;
                    handleProfileClickedInCarrousel(profileName);
                });
            });
        })
        .then(setUnlockCardEvent);
}

function handleProfileClickedInCarrousel(profileName) {
    get(`martians/${getCurrentMarsId()}/profiles/${profileName}`)
        .then(res => res.json())
        .then(profile => {
            if (!checkIfDefaultProfile(profile)) {
                showProfileManager(profile);
                initBreadCrumbs(".profile-manager", profileName);
            } else {
                proposeActivation(profile);
            }

        });
}

async function getProfilesData()
{
    const profileCarrousel = document.querySelector(".profiles-main .profiles-carrousel");
    const unlockCard = document.querySelector("#locked-profile-card-template").content.cloneNode(true);

    const profileCardTemplate = document.querySelector("#profile-card-template").content.cloneNode(true);
    profileCarrousel.innerHTML = profileCardTemplate.outerHTML;

    const profiles =  await get(`martians/${getCurrentMarsId()}/profiles`)
        .then(res => res.json())
        .then(data => {return data;});

    profileCarrousel.innerHTML = "";
    insertUnlockCard(unlockCard, profileCarrousel);
    insertProfiles(profiles, profileCarrousel);
}

function insertProfiles(martianProfiles, htmlTarget)
{
    martianProfiles.forEach(profile => {
        const template = generateTemplate(profile);
        const $unlockProfile = document.querySelector(".profile-card.locked");
        htmlTarget.insertBefore(template, $unlockProfile);
    });
}
function insertUnlockCard(unlockCard, htmlTarget)
{
    htmlTarget.appendChild(unlockCard);
}

function generateTemplate(profileData)
{
    const profileCardTemplate = document.querySelector("#profile-card-template").content.cloneNode(true);
    const profileCardContainer = profileCardTemplate.querySelector(".profile-card");

    if (checkIfDefaultProfile(profileData))
    {
        profileCardContainer.classList.add("default");
    }
    if (profileData.inUse)
    {
        profileCardContainer.classList.add("active");
    }

    const profileTitle = profileCardTemplate.querySelector(".profile-title");
    profileTitle.innerHTML = profileData.name;
    const cardImage = profileCardTemplate.querySelector(".card-image img");
    cardImage.setAttribute("src", `https://avatars.dicebear.com/api/avataaars/${(profileData.name).replace(" ", "%20")}.svg`);
    cardImage.setAttribute("title", profileData.name);
    cardImage.setAttribute("alt", profileData.name);
    const profileTraitList = profileCardTemplate.querySelector(".trait-list>ul");
    const profileTraits = profileData.traits;
    insertProfileTraits(profileData, profileTraitList, profileTraits);

    return profileCardTemplate;

}

function insertProfileTraits(profileData, htmlTraitList, profileTraits)
{
    let possibleTraits;
    if (checkIfDefaultProfile(profileData))
    {
        possibleTraits = profileData.traits.length;
    }
    else
    {
        possibleTraits = profileData.number;
    }

    for (let traitNr = 0; traitNr < possibleTraits; traitNr++) {
        if (traitNr < profileTraits.length)
        {
            htmlTraitList.insertAdjacentHTML("beforeend", `<li class="trait-name">${profileTraits[traitNr].name}</li>`);
        }
        else
        {
            htmlTraitList.insertAdjacentHTML("beforeend", `<li class="trait-name">Empty Slot</li>`);
        }
    }


}

async function unlockProfile()
{
    const numberOfNewProfile = await getNumberOfProfiles();

    if (checkUnlockPermission())
    {
        displayPopupConfirm(`BUY PROFILE NR ${numberOfNewProfile}?`, `The price will be: ${Math.pow(2, numberOfNewProfile)*100} MarsCoins`, "Buy", "Cancel")
            .then(answer => {
                if (answer.action === "true")
                {
                    displayInputAlert("UNLOCK PROFILE", "Name", "Enter a name for your new profile", "Unlock")
                        .then(secondAnswer => {
                            postPromiseData(secondAnswer);
                        });
                }
                return answer;
            });
    }
    else {
        displayPopupAlert("UNLOCK PROFILE", "All profiles must have traits before unlocking a new one.", "Understand");
    }
}

function postPromiseData(answer)
{
    const newProfileName = answer.value;
    const requestBody = {profileName: newProfileName};
    post(`martians/${getCurrentMarsId()}/profiles`, requestBody)
        .then(() => {
            loadCarrouselData();
            setCurrentMartian(getCurrentMarsId());
        });
}

function proposeActivation(profile)
{
    displayPopupConfirm("ACTIVATE", `You want to activate profile ${profile.name}?`, "Yes", "No")
                    .then(answer => {
                        if (answer.action === "true")
                        {
                            activateProfile(profile);
                        }
                        return answer;
                    });
}

function checkUnlockPermission()
{
    return getCurrentMartian().profiles.every(profile => {
        let profileTraitTotal;
        if (checkIfDefaultProfile(profile))
        {
            profileTraitTotal = profile.traits.length;
        }
        else
        {
            profileTraitTotal = profile.number;
        }

        return setCorrectPermission(profile, profileTraitTotal, true);
    });
}

function setCorrectPermission(profile, profileTraitTotal, permission)
{
    if (profile.traits.length === profileTraitTotal)
    {
        permission = true;
        return permission;
    }
    else
    {
        permission = false;
        return permission;
    }
}
