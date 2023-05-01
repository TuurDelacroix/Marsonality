'use strict';

async function loadAllKindOfOwnedTraits()
{
    initSearchForms();
    const $positiveTraitList = document.querySelector("[data-owned-traitlist='pos']");
    const $neutralTraitList = document.querySelector("[data-owned-traitlist='neu']");
    const $negativeTraitList = document.querySelector("[data-owned-traitlist='neg']");

    getTableData($positiveTraitList, $neutralTraitList, $negativeTraitList)
        .then(() => {
            const $posTotal = document.querySelector("#pos-trait-total");
            $posTotal.textContent = $positiveTraitList.children.length.toString();
            const $neuTotal = document.querySelector("#neu-trait-total");
            $neuTotal.textContent = $neutralTraitList.children.length.toString();
            const $negTotal = document.querySelector("#neg-trait-total");
            $negTotal.textContent = $negativeTraitList.children.length.toString();

            initDescriptionView();
        });


}

function initSearchForms()
{
    const $positiveSearchForm = document.querySelector("#search-in-positive-traits");
    const $neutralSearchForm = document.querySelector("#search-in-neutral-traits");
    const $negativeSearchForm = document.querySelector("#search-in-negative-traits");

    const $searchForms = [$positiveSearchForm, $neutralSearchForm, $negativeSearchForm];
    $searchForms.forEach(form => {
        const $inputField = form.querySelector("input[type='text']");
        $inputField.addEventListener('input', (e) => handleSearch(e));
    });
}

function initDescriptionView()
{
    const $ownedTraits = document.querySelectorAll("div.traits-main ul>li div.trait-list li.trait-name");
    $ownedTraits.forEach(trait => {
        trait.addEventListener('click', () => showTraitDescription(trait.id));
    });
}

async function showTraitDescription(traitName)
{
    const traitInfo = await get(`trait/${traitName}`)
                    .then(res => res.json())
                    .then(trait => {return trait;});

    await displayPopupAlert(`${traitName} (${traitInfo.type})`, `${traitInfo.description}`, "Close");
}

async function handleSearch(event)
{
    const $inputField = event.target;
    const $traitListId = $inputField.id.split("-")[0];
    const $filterValue = $inputField.value;

    const traitsToFilter = await getTraitsToFilter($traitListId);

    const $traitList = document.querySelector(`[data-owned-traitlist='${$traitListId}']`);

    const filteredTraits = [];
    traitsToFilter.filter(trait => {
        if (trait.name.toLowerCase().indexOf($filterValue.toLowerCase()) > -1)
        {
            filteredTraits.push(trait);
        }
    });

    insertFilteredTraits(filteredTraits, $traitList);
}

async function getTraitsToFilter($traitListId) {
    let list;
    switch ($traitListId)
    {
        case "pos":
            list = await get(`martians/${getCurrentMarsId()}/traits?traitType=POSITIVE`)
                .then(res => res.json())
                .then(traits => {return traits;});
            break;
        case "neu":
            list = await get(`martians/${getCurrentMarsId()}/traits?traitType=NEUTRAL`)
                .then(res => res.json())
                .then(traits => {return traits;});
            break;
        case "neg":
            list = await get(`martians/${getCurrentMarsId()}/traits?traitType=NEGATIVE`)
                .then(res => res.json())
                .then(traits => {return traits;});
            break;
        default:
            break;
    }

    return list;
}

function insertFilteredTraits(filteredTraits, $htmlTarget)
{
    $htmlTarget.innerHTML = "";
    filteredTraits.forEach(filteredTrait => {
        $htmlTarget.insertAdjacentHTML("beforeend", `<li id="${filteredTrait.name}" class="trait-name">${filteredTrait.name}</li>`);
    });
    initDescriptionView();
}

