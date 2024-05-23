<script>
    export let label;
    export let height;
    export let width;
    export let fontSize;
    export let padding;
</script>

<button on:click  class="font-serif h-{height} bg-primary-50  w-{width} text-{fontSize} p-{padding} rounded-lg text-white hover:bg-primary-200 cursor-pointer">
    {label}
</button>


<style>
    /* button {
        background-color: var(--primary-clr);
        border: none;
        color: white;
        height: var(--height);
        width: var(--width);;
        padding: var(--padd);
        border-radius: 4px;
        font-size: var(--f-size);
    }
    button:hover {
        cursor: pointer;
        background-color: var(--tertiary-clr);
    } */
</style>