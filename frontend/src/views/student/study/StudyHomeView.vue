<template>
    <v-card
            class="table"
    >

        <v-card-text>
            <div>My self-study menu</div>
        </v-card-text>

        <v-tabs vertical
        >

            <v-tabs-slider color="primary"></v-tabs-slider>

            <v-tab>
                <v-icon left>fab fa-leanpub</v-icon>
                Learn More
            </v-tab>
            <v-tab>
                <v-icon left>fas fa-file-contract</v-icon>
                Create Summary
            </v-tab>

            <v-tab-item>
                <v-card flat>
                    <v-card-text>
                        <p>
                            Here you can learn more about a certain topic, and create a quiz to test your knowledge.
                            You can also access your previous created quizzes about each Topic and request a suggested quiz, based in your progress.
                        </p>

                    </v-card-text>

                        <v-btn
                                text
                                color="primary"
                                small
                                :loading="loading"
                                :disabled="loading"
                                @click=loader
                        >
                            Show Suggested Quiz
                        </v-btn>
                        <v-card-text style="position: relative">
                                <span
                                        v-show="!hidden"
                                >sdgfddfdggdf</span>
                        </v-card-text>

                    <v-container fluid>
                        <v-row align="center">
                            <v-col cols="6">
                                <v-subheader >Create Quiz of Topic:</v-subheader>
                            </v-col>
                            <v-col>
                                <v-select
                                        :items="topics"
                                        item-text="name"
                                        menu-props="auto"
                                        label="Select Topic"
                                        hide-details
                                        prepend-icon="map"
                                        single-line
                                        @click=""
                                >
                                </v-select>
                            </v-col>

                        </v-row>
                    </v-container>

                    <br/>

                    <v-card
                            flat
                            class="mx-auto"
                    >
                        <v-toolbar
                                height="37"
                                color="primary"
                                dark

                        >

                            <v-toolbar-title>My Topic Quizzes</v-toolbar-title>

                            <v-spacer></v-spacer>

                            <v-btn small
                                   icon>
                                <v-icon >mdi-magnify</v-icon>
                            </v-btn>

                        </v-toolbar>

                        <v-list two-line flat>
                            <v-list-item-group
                                    v-model="selected"
                                    multiple
                                    active-class="pink--text"
                            >
                                <template v-for="(item, index) in items">
                                    <v-list-item :key="item.title">
                                        <template v-slot:default="{ active, toggle }">
                                            <v-list-item-content>
                                                <v-list-item-title v-text="item.title"></v-list-item-title>
                                                <v-list-item-subtitle class="text--primary" v-text="item.headline"></v-list-item-subtitle>
                                                <v-list-item-subtitle v-text="item.subtitle"></v-list-item-subtitle>
                                            </v-list-item-content>

                                            <v-list-item-action>
                                                <v-list-item-action-text v-text="item.action"></v-list-item-action-text>
                                                <v-icon
                                                        v-if="!active"
                                                        color="grey lighten-1"
                                                >
                                                    star_border
                                                </v-icon>

                                                <v-icon
                                                        v-else
                                                        color="yellow"
                                                >
                                                    star
                                                </v-icon>
                                            </v-list-item-action>
                                        </template>
                                    </v-list-item>

                                    <v-divider
                                            v-if="index + 1 < items.length"
                                            :key="index"
                                    ></v-divider>
                                </template>
                            </v-list-item-group>
                        </v-list>
                    </v-card>

                </v-card>
            </v-tab-item>
            <v-tab-item>
                <v-card flat>
                    <v-card-text>
                        <p>
                            Morbi nec metus. Suspendisse faucibus, nunc et pellentesque egestas, lacus ante convallis tellus, vitae iaculis lacus elit id tortor. Sed mollis, eros et ultrices tempus, mauris ipsum aliquam libero, non adipiscing dolor urna a orci. Curabitur ligula sapien, tincidunt non, euismod vitae, posuere imperdiet, leo. Nunc sed turpis.
                        </p>


                        <p class="mb-0">
                            Donec venenatis vulputate lorem. Aenean viverra rhoncus pede. In dui magna, posuere eget, vestibulum et, tempor auctor, justo. Fusce commodo aliquam arcu. Suspendisse enim turpis, dictum sed, iaculis a, condimentum nec, nisi.
                        </p>

                        <v-card-text>
                            <v-autocomplete
                                    v-model="selectedTopics"
                                    :items="topics"
                                    multiple
                                    return-object
                                    item-text="name"
                                    item-value="name"
                                    label="Topics"
                                    outlined
                                    data-cy="topics"
                            >
                                <template v-slot:selection="data">
                                    <v-chip
                                            v-bind="data.attrs"
                                            :input-value="data.selected"
                                            @click="data.select"
                                            close
                                            @click:close="removeTopic(data.item)"
                                    >
                                        {{ data.item.name }}
                                    </v-chip>
                                </template>
                                <template v-slot:item="data">
                                    <v-list-item-content>
                                        <v-list-item-title v-html="data.item.name" />
                                    </v-list-item-content>
                                </template>
                            </v-autocomplete>

                            <v-btn color="primary"
                                   elevation="5"
                                   @click="newSummary"
                                   data-cy="createButton"
                            >Create</v-btn>

                        </v-card-text>

                    </v-card-text>
                </v-card>

            </v-tab-item>
        </v-tabs>

        <show-summary-dialog
                v-if="summaryDialog"
                :dialog="summaryDialog"
                :topics="selectedTopics"
                v-on:close-show-summary-dialog="onCloseShowSummaryDialog"
        />

    </v-card>

</template>

<script lang="ts">
    import {Component, Vue} from 'vue-property-decorator';
import Topic from '@/models/management/Topic';
    import RemoteServices from '@/services/RemoteServices';
    import ShowSummaryDialog from '@/views/student/study/ShowSummaryDialog.vue';
    import StatementManager from "@/models/statement/StatementManager";

    @Component({
        components: {
            'show-summary-dialog': ShowSummaryDialog,
        }
    })
export default class StudyHomeView extends Vue {
        topics: Topic[] = [];
        summaryDialog: boolean = false;

        selectedTopics: Topic[] = [];

        private loading: boolean = false;
        private hidden: boolean = true;

        statementManager: StatementManager = StatementManager.getInstance;

        async created() {
            this.statementManager.reset();

            this.topics = await RemoteServices.getTopics();
        }

        async createQuiz(topicName: String) {
            try {
                await this.statementManager.getTopicQuizStatement(topicName);
                await this.$router.push({ name: 'solve-quiz' });
            } catch (error) {
                await this.$store.dispatch('error', error);
            }
        }

        removeTopic(topic: Topic) {
            this.selectedTopics = this.selectedTopics.filter(
                element => element.id != topic.id
            );
        }

        onCloseShowSummaryDialog() {
            this.summaryDialog = false;
        }

        newSummary() {
            this.summaryDialog = true;
        }
        data () {
            return {
                hidden: true,
            }
        }

        loader () {
            this.loading = true
            setTimeout(() => (this.loading = false, this.hidden = false), 2500)
        }

    }

</script>

<style lang="scss" scoped>
    .question-textarea {
        text-align: left;

        .CodeMirror,
        .CodeMirror-scroll {
            min-height: 200px !important;
        }
    }
    .option-textarea {
        text-align: left;

        .CodeMirror,
        .CodeMirror-scroll {
            min-height: 100px !important;
        }
    }
</style>
