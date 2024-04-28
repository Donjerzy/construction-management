<script>
    import {page} from '$app/stores';
    const projectId = $page.params.projectId;
    import IndividualProject from '../../../pages/IndividualProject.svelte';
    let overview;

  </script>
  
  <IndividualProject projectId ={projectId} />






