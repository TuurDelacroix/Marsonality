'use strict';
let profileData;

function initDragAndDrop(profile)
{
    profileData = profile;
    const $draggableElements = document.querySelectorAll(".draggable-trait");

    $draggableElements.forEach($element => {
        $element.addEventListener("dragstart", (e) => onDragStartHandler(e, $element));
        $element.addEventListener("dragend", (e) => onDragEndHandler(e, $element));
    });

    const $dropContainers = document.querySelectorAll(".dragdrop-container.drop-container");
    for (const $container of $dropContainers)
    {
        $container.addEventListener("dragover", (e) => onDragOverHandler(e));
        $container.addEventListener("drop", (e) => onDropHandler(e, $container));
    }
}

function onDragStartHandler(e, $htmlElement) {

    const $dragStartContainer = document.getElementById(e.target.parentElement.id);

    switch ($dragStartContainer.id)
    {
        case "additional":
            highlightDirection("right");
            break;
        case "owned":
            highlightDirection("left");
            break;
        default:
            break;
    }
    $htmlElement.classList.add("dragging");
    e.dataTransfer.setData("text/plain", $htmlElement.id);
    e.dataTransfer.setData("dragStart", $dragStartContainer.id);
}

function onDragOverHandler(e)
{
    e.preventDefault();
}

function onDragEndHandler(e, $htmlElement)
{
    e.preventDefault();
    $htmlElement.classList.remove("dragging");
}

function onDropHandler(e, $htmlTarget)
{
    e.preventDefault();
    const $dragStartContainerId = e.dataTransfer.getData("dragStart");
    const $dragStartContainer = document.getElementById($dragStartContainerId);

    const $dropContainer = e.target;

    const $droppedElementId = e.dataTransfer.getData("text/plain");
    const $droppedElement = document.getElementById($droppedElementId);
    const $dropHereElement = document.querySelector(".drag-here");
    const traitName = $droppedElement.id.replace(" ", "%20");

    if (!dragOriginIsEqual($dragStartContainer, $dropContainer))
    {
        moveTrait($dropContainer, traitName, $droppedElement, $dropHereElement, $htmlTarget);
    }

    unHighlightDirection("left");
    unHighlightDirection("right");
}

function moveTrait($dropContainer, traitName, $droppedElement, $dropHereElement, $htmlTarget) {
    switch ($dropContainer.id) {
        case "owned":
            removeTraitFromProfile(traitName, $droppedElement, $dropHereElement, $htmlTarget);
            break;
        case "additional":
            addTraitToProfile(traitName, $droppedElement, $dropHereElement, $htmlTarget);
            break;
        default:
            break;
    }
}

function removeTraitFromProfile(traitName, $droppedElement, $dropHereElement, $htmlTarget)
{
    displayPopupConfirm("REMOVE TRAIT", `Are you sure you want to remove the trait: ${traitName}`, "Yes", "Cancel")
        .then(answer => {
            if (answer.action === "true")
            {
                // Remove trait from profile
                const traitRemoveBody = {
                    name: traitName
                };

                remove(`martians/${getCurrentMarsId()}/profiles/${profileData.name}/trait`, traitRemoveBody)
                    .then(res => console.log(res))
                    .then(() => goBackCarrousel(null))
                    .then(() => setCurrentMartian(getCurrentMarsId()));
                $htmlTarget.insertBefore($droppedElement, $dropHereElement);
            }
            return answer;
        });
}

function addTraitToProfile(traitName, $droppedElement, $dropHereElement, $htmlTarget)
{
    if (checkAdditionPossibility())
    {
        displayPopupConfirm("ADD TRAIT", `Are you sure you want to add the trait: ${traitName}`, "Yes", "Cancel")
            .then(answer => {
                if (answer.action === "true")
                {
                    // Add trait to profile
                    const postBody = {
                        name: traitName
                    };
                    post(`martians/${getCurrentMarsId()}/profiles/${profileData.name}/trait`, postBody)
                        .then(res => console.log(res))
                        .then(() => goBackCarrousel(null))
                        .then(() => setCurrentMartian(getCurrentMarsId()));
                    $htmlTarget.insertBefore($droppedElement, $dropHereElement);
                }
                return answer;
            });
    }
    else
    {
        displayPopupAlert("OOPS", "You can't add more traits to your profile.", "Ok").catch(err => console.log(err));
    }
}

function dragOriginIsEqual($dragStartContainer, $dropContainer)
{
    return $dragStartContainer.id === $dropContainer.id;
}

function highlightDirection(direction)
{
    let $arrow;
    switch (direction)
    {
        case "right":
            $arrow = document.querySelector(`#arrow-${direction}-drag`);
            break;
        case "left":
            $arrow = document.querySelector(`#arrow-${direction}-drag`);
            break;
        default:
            break;
    }

    $arrow.style.color = "#EE7F42FF";
    $arrow.style.opacity = 1;
}

function unHighlightDirection(direction)
{
    let $arrow;
    switch (direction)
    {
        case "right":
            $arrow = document.querySelector(`#arrow-${direction}-drag`);
            break;
        case "left":
            $arrow = document.querySelector(`#arrow-${direction}-drag`);
            break;
        default:
            break;
    }

    $arrow.style.color = "#AAAAAA";
    $arrow.style.opacity = 0.5;
}

function checkAdditionPossibility()
{
    return true;
}
