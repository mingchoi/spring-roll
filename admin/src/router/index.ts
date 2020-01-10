import Vue from "vue";

import Router from "vue-router";
import VueRouter from "vue-router";

import Antd from 'ant-design-vue';
import 'ant-design-vue/dist/antd.css';

import HomePage from "@/components/HomePage.vue";
import AdminPage from "@/components/AdminPage.vue";

Vue.use(Router);
Vue.use(Antd);

const routes = [
    {path: '/', component: HomePage},
    {path: '/admin', component: AdminPage}
];

export default new VueRouter({
    routes
});