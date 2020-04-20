<template>
    <v-dialog
            :value="dialog"
            @input="$emit('dialog', false)"
            @keydown.esc="$emit('dialog', false)"
            max-width="75%"
            max-height="80%"
    >
        <v-card>
            <v-card-title>
                <span class="headline">{{ 'Suggestion' }}</span>
            </v-card-title>
            <v-subheader><bold>Creation Date:</bold></v-subheader>
            <v-card-text class="text-left">
                <span>{{suggestion.creationDate}}</span>
            </v-card-text>

            <v-subheader><bold>Student Username:</bold></v-subheader>
            <v-card-text class="text-left">
                <span>{{suggestion._student.username}}</span>
            </v-card-text>

            <v-subheader><bold>Question Content:</bold></v-subheader>
            <v-card-text class="text-left">
                <span>{{suggestion._questionStr}}</span>
            </v-card-text>

            <v-subheader><bold>Question Topics:</bold></v-subheader>
            <ul>
                <li v-for="option in suggestion._topicsList" :key="option.id">
                    <span class="text-left">{{option.name}}</span>
                </li>
            </ul>

            <h1 v-if="suggestion.status =='TOAPPROVE'">
            <v-subheader>Justification (Optional) </v-subheader>
            <v-flex xs12 sm12 md12>
                <v-textarea
                        outline
                        rows="10"
                        v-model="suggestion._justification"
                        label="Content"
                        data-cy="content"
                ></v-textarea>
            </v-flex>
            </h1>

            <br />

            <v-card-actions>
                <v-spacer />
                <v-btn dark color="blue darken-1" @click="closeQuestionDialog"
                >close</v-btn>

                <h1 v-if="suggestion.status!='REJECTED' && suggestion.status!='APPROVED'">
                <v-btn dark color="green darken-1" @click="ApproveSuggestion(item)"
                >Approve</v-btn>

                <v-btn dark color="red darken-1" @click="RejectSuggestion(item)"
                >Reject</v-btn>

                </h1>

            </v-card-actions>
        </v-card>
    </v-dialog>
</template>

<script lang="ts">
    import { Component, Vue, Prop } from 'vue-property-decorator';
    import Suggestion from '@/models/management/Suggestion';
    import RemoteServices from '@/services/RemoteServices';


    @Component
    export default class ShowSuggDialog extends Vue {
        @Prop({ type: Suggestion, required: true }) readonly suggestion!: Suggestion;
        @Prop({ type: Boolean, required: true }) readonly dialog!: boolean;


        async ApproveSuggestion(sugg: Suggestion) {
            sugg.status = 'APPROVED'
            const result = await RemoteServices.approveSuggestion(sugg);
            this.$emit('approve-question', result);

        }



        async RejectSuggestion(sugg: Suggestion) {
            sugg.status = 'REJECTED'

            if (
                this.suggestion._justification == null) {
                sugg._justification = 'No justification was given';
                return;
            }

            const result = await RemoteServices.approveSuggestion(sugg);
            this.$emit('approve-question', result);
        }



        closeQuestionDialog() {
            console.log(123123123);

            this.$emit('close-show-suggestion-dialog');
        }
    }
</script>
