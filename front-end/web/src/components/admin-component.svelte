<script>
    export let appName;
    export let userFirstName;
</script>


<div class="container">
    <div class="side-bar">
        <div class="sidebar-row">
            <svg id="wheel-barrow" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="M18 15.5C18.83 15.5 19.5 16.17 19.5 17S18.83 18.5 18 18.5 16.5 17.83 16.5 17 17.17 15.5 18 15.5M18 14C16.34 14 15 15.34 15 17S16.34 20 18 20C19.66 20 21 18.66 21 17S19.66 14 18 14M7 8L5 5H2V7H5.13L8 11.3V20H10L12.57 15H14.55C15.25 13.81 16.5 13 18 13C18.88 13 19.69 13.29 20.35 13.78L22 8H7M9.39 19H9V12.8L9.87 14.11C10.23 14.64 10.82 14.96 11.45 15L9.39 19Z" /></svg>
            <h1>{appName}</h1>
        </div>
        <div class="sidebar-content">
            <div class="sidebar-content-row">
                <svg class="sidebar-content-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="M10,20V14H14V20H19V12H22L12,3L2,12H5V20H10Z" /></svg>
                <a href="/" class="sidebar-content-text">Home</a>
            </div>
            <div class="sidebar-content-row">
                <svg class="sidebar-content-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="M21.71 20.29L20.29 21.71A1 1 0 0 1 18.88 21.71L7 9.85A3.81 3.81 0 0 1 6 10A4 4 0 0 1 2.22 4.7L4.76 7.24L5.29 6.71L6.71 5.29L7.24 4.76L4.7 2.22A4 4 0 0 1 10 6A3.81 3.81 0 0 1 9.85 7L21.71 18.88A1 1 0 0 1 21.71 20.29M2.29 18.88A1 1 0 0 0 2.29 20.29L3.71 21.71A1 1 0 0 0 5.12 21.71L10.59 16.25L7.76 13.42M20 2L16 4V6L13.83 8.17L15.83 10.17L18 8H20L22 4Z" /></svg>
                <a href="/projects" class="sidebar-content-text">Projects</a>
            </div>  
        </div>   
    </div>
    <div class="top-nav">
        <div>
            <p class="nav-greeting">Hi there, {userFirstName}</p>
        </div>
        <svg class="account-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="M12,15C7.58,15 4,16.79 4,19V21H20V19C20,16.79 16.42,15 12,15M8,9A4,4 0 0,0 12,13A4,4 0 0,0 16,9M11.5,2C11.2,2 11,2.21 11,2.5V5.5H10V3C10,3 7.75,3.86 7.75,6.75C7.75,6.75 7,6.89 7,8H17C16.95,6.89 16.25,6.75 16.25,6.75C16.25,3.86 14,3 14,3V5.5H13V2.5C13,2.21 12.81,2 12.5,2H11.5Z" /></svg>
    </div>
    <div class="content">
        <slot />
    </div>
</div>


<style>
    :global(body) {
        margin: 0; 
        padding: 0; 
        box-sizing: border-box;
        max-width: 100vw;
        height: 100vh;
        min-height: 100vh;
        --primary-clr: #5c595c;
        --tertiary-clr: #38aa3b;
        font-family: 'Franklin Gothic Medium', 'Arial Narrow', Arial, sans-serif;
    }
    * {
        margin: 0;
        padding: 0;
        box-sizing: border-box;
    }
    .container {
        width: 100%;
        height: 100%;
        display: grid;
        grid-template-columns: 17% 83%;
        grid-template-rows: 12% 78% 10%;
        grid-template-areas: 
            "sidebar topnav"
            "sidebar pgcontent"
            "sidebar pgcontent"
        ;
    }
    .side-bar {
        grid-area: sidebar;
        background-color: var(--primary-clr);
        padding-top: 20px;
        display: flex;
        padding-left: 20px;
        flex-direction: column;
        gap: 80px;
    }
    .side-bar  h1 {
        font-size: 1.4rem;
        width: 140px;
        color: white;
    }
    .sidebar-row {
        display: flex;
        gap: 16px;
        align-items: center;
    }
    #wheel-barrow {
        height: 40px;
        width: 40px;
        fill: white;
    }
    .sidebar-content {
        display: flex;
        flex-direction: column;
        align-items: flex-start;
        padding-left: 20px;
        gap: 16px;
    }
    .sidebar-content-row {
        display: flex;
        gap: 12px;
        align-items: center;
    }
    .sidebar-content-icon {
        height: 28px;
        width: 28px;
        fill: white;
    }
    .sidebar-content-text {
        color: white;
        font-size: 1.2rem;
        text-decoration: none;
    }
    .sidebar-content-text:hover {
        cursor: pointer;
        color: black;
    }
    .top-nav {
        grid-area: topnav;
        background-color: #ffffff;
        padding-left: 40px;
        padding-right: 40px;
        display: flex;
        justify-content: space-between;
        align-items: center;
    }
    .account-icon {
        height: 40px;
        width: 40px;
    }
    .account-icon:hover {
        cursor: pointer;
        fill: var(--tertiary-clr);
    }
    .content {
        grid-area: pgcontent;
        background-color: #e2e8f0;
        padding-left: 40px;
        padding-right: 40px;
        padding-top: 20px;
    }
    .nav-greeting {
        width: 80px;
        font-size: 1rem;
    }
</style>