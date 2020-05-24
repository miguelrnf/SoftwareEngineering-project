<template>
    <v-dialog
            v-model="dialog"
            @keydown.esc="$emit('close-show-summary-dialog', false)"
            max-width="75%"
            max-height="80%"
    >
    </v-dialog>
</template>

<script lang="ts">
    import { Component, Model, Prop, Vue } from 'vue-property-decorator';
    import Topic from '@/models/management/Topic';
    import User from '@/models/user/User';
    import Question from '@/models/management/Question';
    import RemoteServices from '@/services/RemoteServices';

    @Component
    export default class ShowSummaryDialog extends Vue {
        @Prop({ type: Boolean, required: true }) readonly dialog!: boolean;
        @Prop({type: Array, required: true}) readonly topics!: Topic[];
        student: User | null = null;
        questions: Question[] = [];
        
        async created() {
            this.student = await this.$store.getters.getUser;
        }




    }
</script>

<style scoped>

</style>