<script>
    import AdminComponent  from "../components/admin-component.svelte"
    import {firstName, accessToken, loggedIn} from '../stores.js' 
    import { get } from "svelte/store";
    import {onMount} from 'svelte';
    import { Card, Button } from 'flowbite-svelte';
    import { ArrowLeftOutline, ArrowRightOutline } from 'flowbite-svelte-icons';

    let userFirstName = get(firstName);
    let appName = 'Mjengo Bora Construction';
    let contentTitle = "Projects";
    let userSearchValue = '';
    let start = 0;
    let end = 6;
    let projects = [];
    let validProjects = projects.slice(start, end);

    onMount(()=> {
        let errorFetch = false;
        fetch('http://localhost:8080/api/v1/project/all', {
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
                projects = result.setContainer;
                validProjects = projects.slice(start, end);
            }
        })
    });


    function adjustPagination(action) {
        switch(action) {
            case "next":
                start += 6;
                end = start + 6;
                validProjects = projects.slice(start, end);
                break;
            case "previous":
                start -=6;
                end = start + 6;
                validProjects = projects.slice(start, end);
                break;
        }
    }


    function projectSearch() {
        if(userSearchValue === "") {
            validProjects = projects.slice(start, end);
            return
        }
        let cleanedUserValue = userSearchValue.replace(/[^a-zA-Z]/g, '').toLowerCase();
        let matchingArr = [];
        for(let i = 0; i < projects.length; i++) {
            const projectName = projects[i].name.replace(/[^a-zA-Z]/g, '').toLowerCase();
            if(projectName.includes(cleanedUserValue) || cleanedUserValue.includes(projectName)) {
                matchingArr.push(projects[i]);
            }
        }
        validProjects = matchingArr;
        return
    }




</script>


<svelte:head>
    <title>Projects</title>
</svelte:head>




<AdminComponent appName={appName} contentTitle={contentTitle} userFirstName={userFirstName}>
    <div class="bigger-container">
        <div class="search-area">
            <div class="search-box">
                <svg class="magnify" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="M9.5,3A6.5,6.5 0 0,1 16,9.5C16,11.11 15.41,12.59 14.44,13.73L14.71,14H15.5L20.5,19L19,20.5L14,15.5V14.71L13.73,14.44C12.59,15.41 11.11,16 9.5,16A6.5,6.5 0 0,1 3,9.5A6.5,6.5 0 0,1 9.5,3M9.5,5C7,5 5,7 5,9.5C5,12 7,14 9.5,14C12,14 14,12 14,9.5C14,7 12,5 9.5,5Z" /></svg>
                <input bind:value={userSearchValue} type="text" on:change={projectSearch}>
            </div>
            <div>
                <a href="/project/add"><Button color="dark">Add Project</Button></a>    
            </div>
        </div>
        <div class="container">
            {#each validProjects as project }
                    <Card>
                        <h5 class="mb-2 text-2xl font-bold tracking-tight text-gray-900 dark:text-white">{project.name}</h5>
                        <p class="mb-3 font-normal text-gray-700 dark:text-gray-400 leading-tight">{project.status}</p>
                        <a href={`/project/${project.id}`}>
                            <Button class="w-fit">
                                View <ArrowRightOutline class="w-6 h-6 ms-2 text-white" />
                            </Button>
                        </a>
                    </Card>
            {/each}
        </div>
        <div class="pagination">
            {#if start >= 6}
            <Button on:click={()=> adjustPagination("previous")} color="dark" class="w-fit">
                 <ArrowLeftOutline class="w-6 h-6 me-2 text-white" /> Previous
            </Button>
            {/if}
        
            {#if validProjects.length >= (start + 6) }
            <Button on:click={()=> adjustPagination("next")} color="dark" class="w-fit">
                Next <ArrowRightOutline class="w-6 h-6 ms-2 text-white" />
            </Button>
            {/if}
        
        </div>
    </div>
</AdminComponent>


<style>
    .search-area {
        display: flex;
        justify-content: center;
        align-items: center;
        gap: 40px;
    }
    .search-box {
        width: 480px;
        /* border: solid #ccc 1px; */
        margin-top: 4px;
        height: 40px;
        /* background-color: white; */
        border-radius: 20px;
        display: flex;
        align-items: center;
        padding: 8px;
        gap: 4px;
        position: relative;
    }
    .magnify {
        position: absolute;
        height: 28px;
        width: 28px;
        padding-left: 8px;
        top: 50%;
        left: 10px; /* Adjust the left position as per your preference */
        transform: translateY(-50%)
    }
    .search-box input {
        width: 100%;
        height: 40px;
        padding-left: 48px;
        border: solid 1px black;
        border-radius: 20px;
    }
    .container {
        padding-top: 20px;
        display: grid;
        grid-template-columns: 20% 20% 20%;
        column-gap: 20%;
        row-gap: 40px;
        grid-template-rows: 200px 200px;
    }
    
    .pagination {
        padding-top: 24px;
        display: flex;
        justify-content: space-between;
    }
</style>