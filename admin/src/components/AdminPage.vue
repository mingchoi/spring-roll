<template>
    <a-layout id="components-layout-demo-fixed-sider">
        <MenuBar/>
        <UserPage/>
    </a-layout>
</template>


<script lang="ts">
    import {Component, Vue} from "vue-property-decorator";
    import MenuBar from "@/components/MenuBar.vue";
    import UserPage from "@/components/UserPage.vue";

    @Component({
        components: {UserPage, MenuBar}
    })

    export default class AdminPage extends Vue {
    }
</script>

<style scoped>
</style>