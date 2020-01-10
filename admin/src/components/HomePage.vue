<template>
    <div class="container">
        <a-row type="flex" justify="center">
            <a-col :span="6">
                <LoginForm/>
            </a-col>
            <a-col :span="1">
            </a-col>
            <a-col :span="6">
                <RegisterForm/>
            </a-col>
        </a-row>
    </div>
</template>


<script lang="ts">
    import {Component, Vue} from "vue-property-decorator";
    import LoginForm from "@/components/LoginForm.vue";
    import RegisterForm from "@/components/RegisterForm.vue";
    import {State} from "vuex-class";
    import {User} from "@/model/LoginModel";

    @Component({
        components: {RegisterForm, LoginForm}
    })

    export default class HomePage extends Vue {
        @State('message') message: string | undefined;
        @State('user') user: User | undefined;
        @State('token') token: string | undefined;
    }
</script>

<style scoped>
    .container {
    }
</style>