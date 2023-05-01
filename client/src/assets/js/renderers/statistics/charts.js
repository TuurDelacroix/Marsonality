'use strict';

let CHARTS = [];

function mapTraitsToType(traits) {
	const mappedTraits = { POSITIVE: 0, NEGATIVE: 0, NEUTRAL: 0 };

	traits.forEach((trait) => {
		switch (trait.type) {
			case 'POSITIVE':
				mappedTraits.POSITIVE++;
				break;
			case 'NEGATIVE':
				mappedTraits.NEGATIVE++;
				break;
			case 'NEUTRAL':
				mappedTraits.NEUTRAL++;
				break;
			default:
				console.log('Unknown trait type: ' + trait.type);
				break;
		}
	});
	return mappedTraits;
}

async function initCharts() {
    if (CHARTS.length > 0) {
        CHARTS.forEach((chart) => {
            chart.destroy();
        });
        CHARTS = [];
    }

    const usageData = await get(`martians/${getCurrentMarsId()}/profiles/usages`)
        .then((res) => res.json())
        .then((usages) => {
            return usages;
        });
    const traitData = await get(`martians/${getCurrentMarsId()}/traits`)
        .then((res) => res.json())
        .then((traits) => {
            return traits;
        });

    CHARTS.push(makeUsageChart(usageData));
    CHARTS.push(makeTraitChart(mapTraitsToType(traitData)));
}

function makeUsageChart(usageData) {
	const ctx = document.querySelector('#profile-usage-chart').getContext('2d');
	const configuration = {
		type: 'bar',
		data: {
			datasets: [buildUsageData(usageData)],
		},
		options: buildUsageOptions(),
	};
	return new Chart(ctx, configuration);
}

//to make makeChart function a bit smaller and more readable
function buildUsageData(usageData) {
	return {
		label: 'Amount of times this profile was activated',
		data: usageData,
		borderColor: [
			'rgba(235, 110, 63, 1)',
			'rgba(238, 127, 66, 1)',
			'rgba(241, 144, 69, 1)',
			'rgba(244, 161, 72, 1)',
			'rgba(247, 178, 75, 1)',
			'rgba(250, 195, 78, 1)',
		],
		backgroundColor: [
			'rgba(235, 110, 63, 0.6)',
			'rgba(238, 127, 66, 0.6)',
			'rgba(238, 144, 69, 0.6)',
			'rgba(244, 161, 72, 0.6)',
			'rgba(247, 178, 75, 0.6)',
			'rgba(250, 195, 78, 0.6)',
		],
		borderWidth: 1,
	};
}

function getDatasets(traitData) {
    return [
        {
            data: Object.values(traitData),
            backgroundColor: [
                'rgba(235, 110, 63, 1)',
                'rgba(241, 144, 69, 1)',
                'rgba(247, 178, 75, 1)',
            ],
        },
    ];
}

//to make makeChart function a bit smaller and more readable
function buildUsageOptions() {
	return {
		responsive: true,
		plugins: getPlugins(),
		scales: {
			x: getX(),
			y: getY(),
		},
	};
}

function makeTraitChart(traitData) {
	const ctx = document
		.querySelector(
			'#trait-distribution-chart')
		.getContext('2d');
	const configuration = {
		type: 'pie',
		data: {
			labels: Object.keys(traitData),
			datasets: getDatasets(traitData),
		},
		options: {
			plugins: {
				title: {
					display: true,
					text: 'Trait Distribution',
				},
			},
		},
	};
	return new Chart(ctx, configuration);
}

function getPlugins() {
	return {
		title: {
			display: true,
			text: 'Total Profile Usage',
			font: {
				size: 20,
			},
		},
		legend: {
			display: false,
		},
	};
}

function getX() {
	return {
		title: {
			display: true,
			text: 'profiles',
			font: {
				weight: 'bold',
				family: 'Calibri Light',
			},
		},
	};
}

function getY() {
	return {
		beginAtZero: true,
		ticks: {
			stepSize: 1,
		},
		title: {
			display: true,
			text: 'times used',
			font: {
				weight: 'bold',
				family: 'Calibri Light',
			},
		},
	};
}
