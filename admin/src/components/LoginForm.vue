<template>
    <div class="container">
        <h3>Login</h3>
        <a-alert v-if="errorMessage != ''" banner type="error" closable @close="closeErrorMessage">
            <p slot="message">{{errorMessage}}</p>
        </a-alert>
        <a-form :form="form" @submit="attempLogin">
            <a-form-item>
                <a-input v-decorator="['username',{rules: [{ required: true, message: 'Username required' }],
                         initialValue: 'hello'}]"
                         placeholder="Username">
                    <a-icon slot="prefix" type="user" style="color:rgba(0,0,0,.25)"/>
                </a-input>
            </a-form-item>

            <a-form-item>
                <a-input v-decorator="['password', { rules: [{ required: true, message: 'Password required' }],
                         initialValue: 'world'}]"
                         type="password" placeholder="Password">
                    <a-icon slot="prefix" type="lock" style="color:rgba(0,0,0,.25)"/>
                </a-input>
            </a-form-item>

            <a-form-item>
                <a-button type="primary" html-type="submit">Login</a-button>
            </a-form-item>
        </a-form>
    </div>
</template>

<script lang="ts">
    import {Component, Vue} from "vue-property-decorator";
    import {LoginReq, User} from "@/model/LoginModel";
    import {WrappedFormUtils} from "ant-design-vue/types/form/form";

    @Component
    export default class LoginForm extends Vue {

        private errorMessage: String = "";

        private form: WrappedFormUtils | undefined;

        created() {
            this.form = this.$form.createForm(this);
        }

        closeErrorMessage() {
            this.errorMessage = "";
        }

        attempLogin(e: Event) {
            if (this.form !== undefined) {
                this.errorMessage = "";
                let creds: LoginReq = {
                    username: this.form.getFieldValue("username"),
                    password: this.form.getFieldValue("password"),
                };
                this.$store.dispatch("login", creds).then(() => {
                    this.$router.push("/admin");
                }).catch((e) => this.errorMessage = e);
            }
        }
    }
</script>

<style scoped>
</style>
