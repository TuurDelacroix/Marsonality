"use strict";

// Navigation
function redirect(path)
{
    location.href = path + '.html';
}

// Listeners
function listenToSideMenuActivity()
{
    toggleVisibilityOfCharts(false);
    const $sideMenuItems = document.querySelectorAll('#sidebar .side-menu.top li a');

    $sideMenuItems.forEach(item=> {
        const $li = item.parentElement;

        item.addEventListener('click', (event) => {
            changeSideMenu(event, $li, $sideMenuItems);
            if ($li.firstElementChild.dataset.class === ".statistics")
            {
                toggleVisibilityOfCharts(true);
            }
            else {
                toggleVisibilityOfCharts(false);
            }
        });
    });

    const $notificationButton = document.querySelector('a.notifications');
    $notificationButton.addEventListener('click', showNotification($sideMenuItems));
}

function showNotification($sideMenuItems) {
    return (event) => {
        $sideMenuItems.forEach(item => {
            item.parentElement.classList.remove('active');
        });

        event.preventDefault();
        loadNotifications();
        changeVisibility('.notifications-main');
        document.querySelector('h1').textContent = "Notifications";
    };
}

function changeSideMenu(event, listItem, sideMenuItems)
{
    event.preventDefault();

    sideMenuItems.forEach(item => {
        item.parentElement.classList.remove('active');
    });
    if (listItem) {
        listItem.classList.add('active');
    }

    const contentToShow = listItem.firstElementChild.dataset.class;
    changeVisibility(contentToShow);
}


function changeVisibility(htmlTarget)
{
    updateDashboard();
    const $contentsWithoutCurrent = document.querySelectorAll('section>div:not([hidden])');
    const $contentToMakeVisible = document.querySelector(htmlTarget);

    $contentsWithoutCurrent.forEach(content => {
        content.toggleAttribute("hidden");
    });
    $contentToMakeVisible.toggleAttribute("hidden");

    if (htmlTarget !== ".notifications-main")
    {
        initBreadCrumbs(htmlTarget, "");
    }
}

function getBreadCrumbData()
{
    const $crumb = document.querySelector("div.head-title");
    return {
        container: $crumb,
        page_title: $crumb.querySelector("div.left h1"),
        small_navigation_list: $crumb.querySelector(".small-nav"),
        small_title: $crumb.querySelector(".small-nav>li>a"),
    };
}

function getNavigationData(htmlTarget)
{
    return document.querySelector(`${htmlTarget}`).getAttribute("data-crumb-info");
}

function initBreadCrumbs(htmlTarget, profileName)
{
    if (profileName !== "")
    {
        document.querySelector(`${htmlTarget}`).setAttribute('data-crumb-info', `Profiles@Profile@${profileName}`);
    }

    const navigationTarget = getNavigationData(htmlTarget);
    const data = navigationTarget.split("@");
    const title = data[0];
    const subtitle = data[1];
    const extra = data[2];
    const $breadCrumb = getBreadCrumbData();
    setBreadCrumbs(title, subtitle, extra, $breadCrumb);
}

function setBreadCrumbs(title, subtitle, extra, $breadCrumb)
{
    clearBreadCrumbs($breadCrumb);
    $breadCrumb = getBreadCrumbData();
    $breadCrumb.page_title.textContent = title;
    $breadCrumb.small_title.textContent = subtitle;
    $breadCrumb.small_navigation_list.insertAdjacentHTML("beforeend",
        `<li><em class='fas fa-chevron-right'></em></li>
         <li>
            <a class="active" href="#">${extra}</a>
         </li>`);
}

function clearBreadCrumbs($breadCrumb)
{
    $breadCrumb.page_title.textContent = "Dashboard";
    $breadCrumb.small_title.textContent = "Overview";
    $breadCrumb.small_navigation_list.innerHTML = `
                        <li>
                        <a href="#">Dashboard</a>
                    </li>`;
}

function updateDashboard()
{
    getBoxInfoData();
    loadTableData();
    loadAllKindOfOwnedTraits();
    loadAllKindOfShopTraits();
    initCharts();
}

// Profiles

function checkIfDefaultProfile(profile)
{
    return profile.name.includes("DEFAULT");
}

// Chart(s)
function toggleVisibilityOfCharts(visibility)
{
    const $graphs = Object.values(document.querySelector(".statistics").children);
    if (visibility)
    {
        changeDisplayStyle($graphs, "grid");
    }
    else {
        changeDisplayStyle($graphs, "none");
    }

}

function changeDisplayStyle($graphs, style)
{
    $graphs.forEach($graph => {
        $graph.style.display = style;
    });
}
