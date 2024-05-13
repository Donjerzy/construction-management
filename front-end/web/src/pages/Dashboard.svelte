<script>
    import {firstName, accessToken, loggedIn} from '../stores.js' 
    import { get } from "svelte/store";
    import AdminComponent from '../components/admin-component.svelte';
    import { onMount } from 'svelte';
    import Chart from 'chart.js/auto';
    let userFirstName = get(firstName);
    let appName = 'Mjengo Bora Construction';

    let projectStatusValues = [];
    let projectStatusLabels = ['Ongoing', 'Complete', 'Abandoned'];
    let projectStatusCtx;
    let projectStatusCanvas;
    let projectCount = 0;
    let totalAvailableBudget;
    let projectBudgetValues = [];
    let projectBudgetLabels = [];
    let projectBudgetCtx;
    let projectBudgetCanvas;

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
                let budgetData = result.report.projectBudgets;

                for(const budget of budgetData) {
                    projectBudgetLabels.push(budget.name);
                    projectBudgetValues.push(budget.budget);
                }

                projectCount = result.report.ongoingProjects + result.report.completeProjects + result.report.abandonedProjects;
                totalAvailableBudget = result.report.totalAvailableBudget;
                projectStatusCtx = projectStatusCanvas.getContext('2d');
                projectBudgetCtx = projectBudgetCanvas.getContext('2d');

                let chart = new Chart(projectStatusCtx, {
				type: 'doughnut',
				data: {
						labels: projectStatusLabels,
						datasets: [{
								backgroundColor: [
						      'rgb(255, 99, 132)',
						      'rgb(54, 162, 235)',
						      'rgb(255, 205, 86)'
						    ],
								data: projectStatusValues
						}]
				}
		        });


                let projectBudgetChart = new Chart(projectBudgetCanvas, {
				type: 'doughnut',
				data: {
						labels: projectBudgetLabels,
						datasets: [{
								backgroundColor: [
                              'rgb(255, 0, 0)',
                              'rgb(0, 255, 0)',
                              'rgb(0, 0, 255)',
                              'rgb(0, 255, 255)',
                              'rgb(255, 0, 255)',
                              'rgb(255, 255, 0)',
                              'rgb(0, 0, 0)',
                              'rgb(255, 255, 255)',
                              'rgb(128, 128, 128)',
                              'rgb(192, 192, 192)',
                              'rgb(64, 64, 64)',
                              'rgb(128, 0, 0)',
                              'rgb(128, 128, 0)',
                              'rgb(0, 128, 0)',
                              'rgb(128, 0, 128)',
						      'rgb(255, 99, 132)',
						      'rgb(54, 162, 235)',
						      'rgb(255, 205, 86)'
						    ],
								data: projectBudgetValues
						}]
				}
		        });





            }
        })
    });







</script>


<svelte:head>
    <title>Dashboard</title>
</svelte:head>


<AdminComponent appName={appName} userFirstName={userFirstName} contentTitle="Dashboard">
    <div id="container">

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

    </div>
</AdminComponent>


<style>
    #container {
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
</style>