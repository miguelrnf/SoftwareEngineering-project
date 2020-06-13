<template>
    <v-dialog
            :value="dialog"
            @input="closeDialog"
            @keydown.esc="closeDialog"
            max-width="80%"

    >
        <v-card class="mx-auto"
        >
            <v-card-title>Evaluation Settings</v-card-title>

            <v-container fluid>
                <v-row align="center">
                    <v-col cols="6">
                        <v-subheader>Scale (0 - X)</v-subheader>
                    </v-col>

                    <v-col cols="6">
                        <v-select
                                v-model="select"
                                :items="items"
                                item-text="state"
                                item-value="abbr"
                                label="Select"
                                persistent-hint
                                return-object
                                single-line
                        ></v-select>
                    </v-col>
                </v-row>
            </v-container>

            <v-subheader>Evaluation percentages:</v-subheader>
            <br>
            <v-card-text class="pt-0">
                <v-slider
                        v-model="quizzes"
                        :rules="rulesQuizzes"
                        label="Evaluation Quizzes (%)"
                        step="5"
                        thumb-label="always"
                        ticks
                ></v-slider>
            </v-card-text>

            <v-card-text class="pt-0">
                <v-slider
                        v-model="tournament"
                        :rules="rulesTournament"
                        label="Tournaments (%)"
                        persistent-hint
                        step="5"
                        thumb-label="always"
                        ticks
                ></v-slider>
            </v-card-text>


            <v-card-text class="pt-0">
                <v-slider
                        v-model="suggestions"
                        :rules="rulesSuggestions"
                        label="Approved Suggestions (%)"
                        persistent-hint
                        step="5"
                        thumb-label="always"
                        ticks
                ></v-slider>
            </v-card-text>

            <v-subheader inset v-if="(suggestions+tournament+quizzes)<100" >{{100-(suggestions+tournament+quizzes)}} % left to assign</v-subheader>

            <v-card-actions>
                <v-spacer />
                <v-btn
                        color="primary"
                        text
                        @click="closeDialog"
                        data-cy="cancel"
                >Cancel</v-btn>
                <v-btn
                        color="primary"
                        text
                        @click="save"
                        data-cy="save"
                >Save</v-btn>

            </v-card-actions>


        </v-card>
    </v-dialog>
</template>

<script lang="ts">
    import Classroom from "@/models/management/Classroom";
    import {getIdFromURL} from "vue-youtube-embed";
    import {Component, Model, Vue} from "vue-property-decorator";
    import LazyYoutubeVideo from "vue-lazy-youtube-video";
    import User from "@/models/user/User";
    import EvalSettings from "@/models/management/EvalSettings";
    import RemoteServices from "@/services/RemoteServices";




    @Component
    export default class EditEvalSettingsDialog extends Vue {
        @Model('dialog', Boolean) dialog!: boolean;


        quizzes: number = 0;
        suggestions: number = 0;
        tournament: number = 0;

        settings: EvalSettings | null = null;

        select!: {
            state: string;
            abbr: number;
        }

        items: [{
            state: '0 - 20';
            abbr: 20;
        }, {
            state: '0 - 100';
            abbr: 100;
        }, {
            state: '0 - 10';
            abbr: 10;
        }, {
            state: '0 - 5';
            abbr: 5;
        }] | undefined

         async created() {
             this.settings = await RemoteServices.getEvalSettings()
             this.quizzes = this.settings.quizWeight;
             this.tournament = this.settings.tournamentWeight;
             this.suggestions = this.settings.suggWeight;

             if(this.settings.scale == 20 && this.items)
                 this.select = this.items[0];
             if(this.settings.scale == 100 && this.items)
                 this.select = this.items[1];
             if(this.settings.scale == 10 && this.items)
                 this.select = this.items[2];
             if(this.settings.scale == 5 && this.items)
                 this.select = this.items[3];
         }

        closeDialog() {
            this.$emit('close-edit-lecture-dialog');
        }

        async save(){
            if(this.settings != null) {
                this.settings.quizWeight = this.quizzes;
                this.settings.suggWeight = this.suggestions
                this.settings.suggWeight = this.suggestions;
            }

            if( this.settings != null) {
                this.settings.scale = this.select?.abbr;

                await RemoteServices.changeEvalSettings(this.settings);
                this.$emit('close-edit-lecture-dialog');
            }
        }


        data () {
            return {
                select: { state: '0 - 20', abbr: 20 },
                items: [
                    { state: '0 - 20', abbr: 20 },
                    { state: '0 - 100', abbr: 100 },
                    { state: '0 - 10', abbr: 10 },
                    { state: '0 - 5', abbr: 5 },
                ],
                rulesQuizzes: [
                    v => v <= 100-(this.tournament+this.suggestions) || 'Max',
                ],rulesTournament: [
                    v => v <= 100-(this.quizzes+this.suggestions) || 'Max',
                ],rulesSuggestions: [
                    v => v <= 100-(this.quizzes+this.tournament) || 'Max',
                ],
            }
        }
    }

</script>

<style lang="scss" scoped>

</style>