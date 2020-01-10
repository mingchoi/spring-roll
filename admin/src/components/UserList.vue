<template>
    <div>
        <h3>UserList</h3>
        <input type="button" value="FindAllUser" v-on:click="findAllUser"/>
        <table>
            <tr>
                <th>id</th>
                <th>username</th>
                <th>age</th>
                <th>vip</th>
            </tr>
            <tr v-for="user in userList" v-bind:key="user.id">
                <td>{{ user.id }}</td>
                <td>{{ user.username }}</td>
                <td>{{ user.age }}</td>
                <td>{{ user.vip }}</td>
            </tr>
        </table>
    </div>
</template>

<script lang="ts">
    import {Component, Vue} from "vue-property-decorator";
    import {State} from "vuex-class";
    import {User} from "@/model/LoginModel";

    @Component
    export default class UserList extends Vue {
        @State("userList") userList: Array<User>

        findAllUser(e: Event) {
            this.$store.dispatch("findAllUser");
        }
    }
</script>