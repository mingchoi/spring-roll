<template>
    <div>
        <h3>Register</h3>
        <a-alert v-if="errorMessage != ''" banner type="error" closable @close="closeErrorMessage">
            <p slot="message">{{errorMessage}}</p>
        </a-alert>
        <a-form :form="form" @submit="register">
            <a-form-item>
                <a-input v-decorator="['username', { rules: [{ required: true, message: 'Username required' }],
                initialValue: 'hello'}]"
                         placeholder="Username">
                    <a-icon slot="prefix" type="user" style="color:rgba(0,0,0,.25)"/>
                </a-input>
            </a-form-item>

            <a-form-item>
                <a-input v-decorator="['password', { rules: [{ required: true, message: 'Password required' }],
                 initialValue: 'world'}]" type="password" placeholder="Password">
                    <a-icon slot="prefix" type="lock" style="color:rgba(0,0,0,.25)"/>
                </a-input>
            </a-form-item>

            <a-form-item>
                <a-input v-decorator="['email', { rules: [{ required: true, message: 'Email required' }],
                 initialValue: 'hello@world.com'}]" placeholder="Email">
                    <a-icon slot="prefix" type="mail" style="color:rgba(0,0,0,.25)"/>
                </a-input>
            </a-form-item>

            <a-form-item style="margin-bottom:0;">
                <a-form-item :style="{ display: 'inline-block', width: 'calc(50%)' }">
                    <a-input-number v-decorator="['age', { rules: [{ required: true, message: 'Age required'}],
                    initialValue: 18}]"
                                    :min="0" :max="150" placeholder="Age"/>
                </a-form-item>

                <a-form-item :style="{ display: 'inline-block', width: 'calc(50%)' }">
                    <a-checkbox-group v-decorator="['vip', { initialValue: [] }]">
                        <a-checkbox value="True">VIP</a-checkbox>
                    </a-checkbox-group>
                </a-form-item>
            </a-form-item>

            <a-form-item>
                <a-button type="primary" html-type="submit">Register</a-button>
            </a-form-item>
        </a-form>
    </div>
</template>

<script lang="ts">
    import {Component, Vue} from "vue-property-decorator";
    import {LoginReq, RegisterReq, User} from "@/model/LoginModel";
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

        register(e: Event) {
            if (this.form !== undefined) {
                this.errorMessage = "";
                let form: RegisterReq = {
                    username: this.form.getFieldValue("username"),
                    password: this.form.getFieldValue("password"),
                    email: this.form.getFieldValue("email"),
                    age: this.form.getFieldValue("age"),
                    vip: this.form.getFieldValue("vip").includes("True")
                };
                this.$store.dispatch("register", form).then(() =>
                    this.attempLogin()
                ).catch(e => this.errorMessage = e);
            }
        }

        attempLogin() {
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