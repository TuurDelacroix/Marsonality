'use strict';

async function getBoxInfoData()
{
    const profilesHeadBoxInfo = document.querySelector(".box-info-head");
    const currentActiveProfile = profilesHeadBoxInfo.querySelector("[data-info='currentProfileName'] p");
    currentActiveProfile.innerHTML = await getCurrentlyUsingProfile();

    const profilesBoxInfo = document.querySelector(".box-info");
    const numberOfProfiles = profilesBoxInfo.querySelector("[data-info='numberOfProfiles'] p");
    numberOfProfiles.innerHTML = await getNumberOfProfiles();
    const numberOfTraits = profilesBoxInfo.querySelector("[data-info='numberOfTraits']>p");

    const defaultTraits = numberOfTraits.querySelector("#default-num-traits");
    defaultTraits.innerHTML = `DEFAULT: ${await getNumberOfDefaultTraits()}`;
    const additionalTraits = numberOfTraits.querySelector("#add-num-traits");
    additionalTraits.innerHTML = `ADDITIONAL: ${await getNumberOfAdditionalTraits()}`;

    const mostUsedProfiles = profilesBoxInfo.querySelector("[data-info='mostUsedProfile'] p");
    mostUsedProfiles.innerHTML = await getMostUsedProfile();
}



async function getNumberOfProfiles()
{
    let numberOfProfiles;
    try {
        numberOfProfiles = await get(`martians/${getCurrentMarsId()}/profiles`)
                        .then(res => res.json())
                        .then(profiles => {return profiles.length;});
    } catch (err)
    {
        console.error(err);
    }

    return numberOfProfiles;
}


async function getNumberOfDefaultTraits()
{
    let numberOfTraits;
    try {
        numberOfTraits = await get(`martians/${getCurrentMarsId()}/profiles/default`)
            .then(res => res.json())
            .then(profile => {
                return profile.traits.length;
            });
    } catch (err)
    {
        console.error(err);
    }

    return numberOfTraits;
}

async function getNumberOfAdditionalTraits()
{
    let numberOfTraits;
    try {
        numberOfTraits = await get(`martians/${getCurrentMarsId()}/traits`)
            .then(res => res.json())
            .then(traits => {
                return traits.length;
            });
    } catch (err)
    {
        console.error(err);
    }

    return numberOfTraits;
}

async function getMostUsedProfile()
{
    try {
        const profileUsages = await get(`martians/${getCurrentMarsId()}/profiles/usages`)
                .then(res => res.json())
                .then(usages => {return usages;});

        const maxProfileUsage = Math.max(...Object.values(profileUsages));

        return Object.keys(profileUsages).find(key => profileUsages[key] === maxProfileUsage);

    } catch (err)
    {
        console.error(err);
        return null;
    }
}

async function getCurrentlyUsingProfile()
{
    try {
        return await get(`martians/${getCurrentMarsId()}/profiles/active`)
            .then(res => res.json())
            .then(profile => {return profile.name;});
    } catch (err)
    {
        console.error(err);
        return null;
    }
}
