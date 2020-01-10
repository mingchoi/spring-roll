<template>
    <a-layout-sider :style="{ overflow: 'auto', height: '100vh', position: 'fixed', left: 0 }">
        <div class="logo">
            <router-link to="/">Admin</router-link>
        </div>
        <a-menu theme="dark" mode="inline" :defaultSelectedKeys="['1']">
            <a-menu-item key="1">
                <a-icon type="user"/>
                <span class="nav-text">nav 1</span>
            </a-menu-item>
            <a-menu-item key="2">
                <a-icon type="video-camera"/>
                <span class="nav-text">nav 2</span>
            </a-menu-item>
            <a-menu-item key="3">
                <a-icon type="upload"/>
                <span class="nav-text">nav 3</span>
            </a-menu-item>
            <a-menu-item key="4">
                <a-icon type="bar-chart"/>
                <span class="nav-text">nav 4</span>
            </a-menu-item>
        </a-menu>
    </a-layout-sider>
</template>

<script lang="ts">
    import {Component, Vue} from "vue-property-decorator";

    @Component
    export default class MenuBar extends Vue {

    }
</script>

<style scoped>
    .logo {
        color: #fff;
        font-size: 30pt;
    }
</style>