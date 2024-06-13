<script>
    import EmployeeComponent from "../components/employee-component.svelte";
    import {firstName, accessToken, loggedIn, projectClient} from '../stores.js' 
    import { get } from "svelte/store";
    import { onMount } from "svelte";
    import { Card, Chart } from 'flowbite-svelte';
    let contentTitle = "Home";
    let incompleteTaskCount;
    let averageTaskCompletionTime = 'n/a';
    let taskStatusLabels = ['Todo', 'In-progress', 'Done'];
    let options = {
        series: [],
        colors: ['#1C64F2', '#16BDCA', '#9061F9'],
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
        labels: taskStatusLabels,
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


    onMount(()=> {
        let errorFetch = false;
        fetch(`http://localhost:8080/api/v1/employee/task/incomplete`, {
            headers: {
                'Cmt': `CMT ${get(accessToken)}`
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
                incompleteTaskCount = result.count;
            }
        })

        fetch(`http://localhost:8080/api/v1/employee/report/dashboard`, {
            headers: {
                'Cmt': `CMT ${get(accessToken)}`
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
                averageTaskCompletionTime = result.report.averageTaskCompletionTime;
                options['series'] = [result.report.toDo, result.report.inProgress, result.report.done]
            }
        })
    })

    function numberWithCommas(x) {
        return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
    }

</script>


<EmployeeComponent contentTitle={contentTitle}>
    <div class="mt-10 flex items-center justify-center">
        <p class="font-mono text-xl">Welcome, {get(firstName)}</p>
    </div>
    <div class="mt-5 min-h-[400px] flex items-center w-full justify-center gap-20">
        <Card class="h-72">
            <div class="flex justify-between items-start w-full">
              <div class="flex-col items-center w-full">
                <div class="flex items-center w-full justify-center mb-1">
                  <h5 class="text-xl font-bold leading-none text-gray-900 dark:text-white me-1">Tasks</h5> 
                </div>
              </div>
            </div>
            <Chart {options} class="py-6" />
        </Card>

        <Card class="h-72 w-full font-bold font-serif flex items-center">
            <h5 class="text-xl font-bold leading-none text-gray-900">Average task completion time (in minutes)</h5>
            <div class="flex flex-col h-full justify-center">
                <p class="text-center text-5xl">{averageTaskCompletionTime === 'n/a' ? 'n/a' : numberWithCommas(averageTaskCompletionTime)}</p>
            </div> 
        </Card>   
    </div>


    <div class="flex gap-5 items-center justify-center">
        <p class="font-mono">You have {incompleteTaskCount} incomplete {incompleteTaskCount === 1 ? "task" : "tasks"}</p>
        <a href="/employee/task">
            <button class="p-3 font-mono w-40 border border-blue-400 rounded-lg shadow-lg hover:cursor-pointer hover:bg-slate-500 hover:text-white">Tasks</button>
            </a>
    </div>
</EmployeeComponent>