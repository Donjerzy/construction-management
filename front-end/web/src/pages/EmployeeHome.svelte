<script>
    import EmployeeComponent from "../components/employee-component.svelte";
    import {firstName, accessToken, loggedIn, projectClient} from '../stores.js' 
    import { get } from "svelte/store";
    import { onMount } from "svelte";
    let contentTitle = "Home";
    let incompleteTaskCount;


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
    })
















</script>


<EmployeeComponent contentTitle={contentTitle}>
    <div class="w-full min-h-[400px] h-full flex-col gap-5  mt-5 flex justify-center items-center">
        <p class="font-mono">Hi there, {get(firstName)}</p>
        <div class="flex gap-5 items-center">
            <p class="font-mono">You have {incompleteTaskCount} incomplete tasks</p>
            <a href="/employee/task">
                <button class="p-3 font-mono w-40 border border-blue-400 rounded-lg shadow-lg hover:cursor-pointer hover:bg-slate-500 hover:text-white">Tasks</button>
                </a>
             </div>
        
    </div>
</EmployeeComponent>