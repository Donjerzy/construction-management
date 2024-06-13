<script>
    import {firstName, accessToken, loggedIn} from '../stores.js' 
    import { get } from "svelte/store";
    import AdminComponent from '../components/admin-component.svelte';
    import { Card, Chart } from 'flowbite-svelte';
    import { onMount } from 'svelte';
    let userFirstName = get(firstName);
    let appName = 'Mjengo Bora Construction';

    let projectStatusValues = [];
    let projectStatusLabels = ['Ongoing', 'Complete'];
    let projectStatusCtx;
    let projectStatusCanvas;
    let projectCount = 0;
    let totalAvailableBudget;
    let projectBudgetValues = [];
    let projectBudgetLabels = [];
    let projectBudgetCtx;
    let projectBudgetCanvas;



    let options = {
        series: [],
        colors: ['#1C64F2', '#16BDCA'],
        chart: {
        height: '100%',
        width: '100%',
        type: 'pie'
        },
        stroke: {
        colors: ['white'],
        lineCap: ''
        },
        plotOptions: {
        pie: {
            labels: {
            show: true
            },
            size: '100%',
            dataLabels: {
            offset: -25
            }
        }
        },
        labels: projectStatusLabels,
        dataLabels: {
        enabled: true,
        style: {
            fontFamily: 'Inter, sans-serif'
        }
        },
        legend: {
        position: 'bottom',
        fontFamily: 'Inter, sans-serif'
        },
        yaxis: {
        labels: {
            formatter: function (value) {
            return value;
            }
        }
        },
        xaxis: {
        labels: {
            formatter: function (value) {
            return value + '%';
            }
        },
        axisTicks: {
            show: false
        },
        axisBorder: {
            show: false
        }
        }
  };






    function numberWithCommas(x) {
        return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
    }


    onMount(()=> {
        let errorFetch = false;
        fetch('http://localhost:8080/api/v1/report/dashboard', {
            headers: {
                'Authorization': `Bearer ${get(accessToken)}`
            }
        })
        .then(response => {
            if(!response.ok) {
                errorFetch = true;
               firstName.set("");
               accessToken.set("");
               loggedIn.set("false");
               window.location.replace('/'); 
            } else {
                return response.json();
            }
        }).then((result)=> {
            if(!errorFetch) {
                projectStatusValues.push(result.report.ongoingProjects);
                projectStatusValues.push(result.report.completeProjects);
                projectStatusValues.push(result.report.abandonedProjects);
                options['series'] = [result.report.ongoingProjects, result.report.completeProjects];
                projectCount = result.report.completeProjects + result.report.ongoingProjects + result.report.abandonedProjects;
            }
        })
    });







</script>


<svelte:head>
    <title>Dashboard</title>
</svelte:head>


<AdminComponent appName={appName} userFirstName={userFirstName} contentTitle="Dashboard">

    <div class="mt-10 flex items-center justify-center">
        <p class="font-mono text-xl">Welcome, {get(firstName)}</p>
    </div>


    <div class="mt-5 min-h-[400px] flex items-center w-full justify-center gap-20">
        <Card class="h-72 w-full font-bold font-serif flex items-center">
            <h5 class="text-xl font-bold leading-none text-gray-900">Number of Projects</h5>
            <div class="flex flex-col h-full justify-center">
                <p class="text-center text-5xl">{numberWithCommas(projectCount)}</p>
            </div> 
        </Card>

        <Card class="h-72">
            <div class="flex justify-between items-start w-full">
              <div class="flex-col items-center w-full">
                <div class="flex items-center w-full justify-center mb-1">
                  <h5 class="text-xl font-bold leading-none text-gray-900 dark:text-white me-1">Projects</h5> 
                </div>
              </div>
            </div>
            <Chart {options} class="py-6" />
        </Card>
    </div>

    <div class="flex items-center justify-center gap-5">
        <p class="font-mono">Pick up from exactly where you left off!</p>
        <a href="/project">
            <button class="p-3 font-mono w-40 border border-blue-400 rounded-lg shadow-lg hover:cursor-pointer hover:bg-slate-500 hover:text-white">Projects</button>
            </a>
    </div>
    



    <!-- <div id="container">
        <div id="projects">
            <p>Projects</p>
        </div>

        <div id="projects_content">
            <div style="display: flex; flex-direction: column; justify-content: flex-start;">
                <p class="text-xs w-fit m-auto mb-0">Total</p>
                <p class="text-9xl w-fit m-auto mt-0 p-0">{projectCount}</p>
            </div> 
            <canvas class="max-h-64 max-w-64" id="projectStatus" bind:this={projectStatusCanvas}></canvas>
        </div>

        <div id="budget">
            <p>Budget</p>
        </div>

        <div id="budget_content">
            <div style="display: flex; flex-direction: column; justify-content: flex-start;">
                <p class="text-xs w-fit m-auto">Total Available Budget</p>
                <p class="text-3xl m-auto  w-fit">{numberWithCommas(totalAvailableBudget === undefined ? 0 : totalAvailableBudget)}</p>
            </div> 
            <canvas class="max-h-64 max-w-64" id="projectStatus" bind:this={projectBudgetCanvas}></canvas>
        </div>
    </div> -->

</AdminComponent>


<!-- <style>
    #container {
        margin-top: 20px;
        height: 90%;
        max-height: 100%;
        width: 90%;
        display: grid;
        grid-template-rows: 50%  50%;
        grid-template-columns: 20% 80%;
        grid-template-areas: 
            "projects project_charts"
            "budget budget_charts"
            ;
        padding-top: 20px;
    }
    #projects {
        grid-area: projects;
        padding-top: 20px;
        padding-left: 20px;
    }
    #budget {
        grid-area: budget;
        padding-top: 20px;
        padding-left: 20px;
    }
    #projects_content {
        grid-area: project_charts;
        display: flex;
        align-items: center;
        padding-left: 80px;
        justify-content: space-between;
    }
    #budget_content {
        grid-area: budget_charts;
        display: flex;
        align-items: center;
        padding-left: 80px;
        justify-content: space-between;
    }
</style> -->