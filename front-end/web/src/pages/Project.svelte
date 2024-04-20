<script>
    import AdminComponent  from "../components/admin-component.svelte"
    import {firstName} from '../stores.js' 
    import { get } from "svelte/store";
    let userFirstName = get(firstName);
    let appName = 'Mjengo Bora Construction';
    let contentTitle = "Projects";
    let userSearchValue = '';
    let start = 0;
    let end = 6;
    let demoProjects = [
        {
            name: "Nairobi West",
            status: "ONGOING"
        },
        {
            name: "komarock",
            status: "DONE"
        },
        {
            name: "Langata Suites",
            status: "ONGOING"
        },
        {
            name: "Nairobi West",
            status: "ONGOING"
        },
        {
            name: "Kilimanjaro heights",
            status: "ONGOING"
        },
        {
            name: "Lavington",
            status: "ONGOING"
        },
        {
            name: "Mombasa",
            status: "ONGOING"
        },
    ]
    let validProjects = demoProjects.slice(start, end);
    function adjustPagination(action) {
        switch(action) {
            case "next":
                start += 6;
                end = start + 6;
                validProjects = demoProjects.slice(start, end);
                break;
            case "previous":
                start -=6;
                end = start + 6;
                validProjects = demoProjects.slice(start, end);
                break;
        }
    }


    function projectSearch() {
        if(userSearchValue === "") {
            validProjects = demoProjects.slice(start, end);
            return
        }
        let cleanedUserValue = userSearchValue.replace(/[^a-zA-Z]/g, '').toLowerCase();
        let matchingArr = [];
        for(let i = 0; i < demoProjects.length; i++) {
            const projectName = demoProjects[i].name.replace(/[^a-zA-Z]/g, '').toLowerCase();
            if(projectName.includes(cleanedUserValue) || cleanedUserValue.includes(projectName)) {
                matchingArr.push(demoProjects[i]);
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
        </div>
        <div class="container">
            {#each validProjects as project }
                <div class="project-container">
                    <div class="strip"></div>
                    <div class="project-items">
                        <p class="card-text">{project.name}</p>
                        <p class="card-text">{project.status}</p>
                    </div>
        
                </div>
            {/each}
        </div>
        <div class="pagination">
            {#if start >= 6}
            <button on:click={()=> adjustPagination("previous")} class="pagination-btn">
                <svg class="button-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="M20,11V13H8L13.5,18.5L12.08,19.92L4.16,12L12.08,4.08L13.5,5.5L8,11H20Z" /></svg>
                <p class="button-text">Previous</p>
            </button>
            {/if}
        
            {#if validProjects.length >= (start + 6) }
            <button on:click={()=> adjustPagination("next")} class="pagination-btn">
                <p class="button-text">Next</p>
                <svg class="button-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="M4,11V13H16L10.5,18.5L11.92,19.92L19.84,12L11.92,4.08L10.5,5.5L16,11H4Z" /></svg>
            </button>
            {/if}
        
        </div>
    </div>
</AdminComponent>


<style>
    .search-area {
        display: flex;
        justify-content: center;
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
        border: none;
        border-radius: 20px;
    }
    input:focus, input:active, input:hover {
        border: none;
    }
    .container {
        padding-top: 20px;
        display: grid;
        grid-template-columns: 20% 20% 20%;
        column-gap: 20%;
        row-gap: 40px;
        grid-template-rows: 200px 200px;
    }
    .project-container {
        background-color: white;
        border-radius: 4px;
        border-left-width: 12px;   
        display: flex;
        gap: 16px;
    }
    .project-container:hover {
        background-color: #eeeee4;
        cursor: pointer;
    }
    .pagination {
        padding-top: 24px;
        display: flex;
        justify-content: space-between;
    }
    .pagination-btn {
        display: flex;
        width: 120px;
        height: 40px;
        align-items: center;
        border-radius: 4px;
        justify-content: center;
        border: none;
        background-color: var(--primary-clr);
        color: white;
        padding: 4px;
        gap: 8px;
    } 
    .pagination-btn:hover {
        background-color: var(--tertiary-clr);
        cursor: pointer;
    }
    .button-icon {
        height: 28px;
        width: 28px;
        fill: white;
    }
    .strip {
        display: flex;
        flex-direction: column;
        width: 12px;
        height: 100%;
        background-color: #f0b429;
    }
    .project-items {
        display: flex;
        flex-direction: column;
        justify-content: space-between;
    }
    .card-text {
        font-size: 1.1rem;
    }
</style>