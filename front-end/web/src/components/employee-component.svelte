<script>
    export let contentTitle;
    let appName = "Mjengo Bora Construction";
</script>


<div>
    <nav class="flex bg-primary-50 h-12 pl-5 w-full items-center justify-between pr-5">
        <div class="flex items-center gap-3">
            <svg class="fill-white h-7 w-7"  xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="M18 15.5C18.83 15.5 19.5 16.17 19.5 17S18.83 18.5 18 18.5 16.5 17.83 16.5 17 17.17 15.5 18 15.5M18 14C16.34 14 15 15.34 15 17S16.34 20 18 20C19.66 20 21 18.66 21 17S19.66 14 18 14M7 8L5 5H2V7H5.13L8 11.3V20H10L12.57 15H14.55C15.25 13.81 16.5 13 18 13C18.88 13 19.69 13.29 20.35 13.78L22 8H7M9.39 19H9V12.8L9.87 14.11C10.23 14.64 10.82 14.96 11.45 15L9.39 19Z" /></svg>
            <p class="text-white text-base font-serif">{appName}</p>
        </div>


        <div class="flex gap-5">
            <a href="/employee/home" class="text-white hover:text-primary-200 font-serif">Home</a>
            <a href="/employee/task" class="text-white hover:text-primary-200 font-serif">Tasks</a>
            <a href="/employee/expense" class="text-white hover:text-primary-200 font-serif">Expenses</a>
            <!-- <a href="/employee/home" class="text-white hover:text-primary-200 font-serif">Reports</a> -->
        </div>

        <div>
            <a href="/employee/profile"><svg class="h7 w-7 fill-white hover:cursor-pointer hover:fill-primary-200" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="M12,15C7.58,15 4,16.79 4,19V21H20V19C20,16.79 16.42,15 12,15M8,9A4,4 0 0,0 12,13A4,4 0 0,0 16,9M11.5,2C11.2,2 11,2.21 11,2.5V5.5H10V3C10,3 7.75,3.86 7.75,6.75C7.75,6.75 7,6.89 7,8H17C16.95,6.89 16.25,6.75 16.25,6.75C16.25,3.86 14,3 14,3V5.5H13V2.5C13,2.21 12.81,2 12.5,2H11.5Z" /></svg>
            </a>
        </div>
        
    </nav>
    <div class="w-full h-full p-5">
        <div>
            <h1 class="text-lg font-serif">{contentTitle}</h1>
        </div>
        <slot class="pt-5" />
    </div>
</div>