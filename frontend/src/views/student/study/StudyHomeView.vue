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
                            Sed aliquam ultrices mauris. Donec posuere vulputate arcu. Morbi ac felis. Etiam feugiat lorem non metus. Sed a libero.
                        </p>

                        <p class="mb-0">
                            Phasellus dolor. Fusce neque. Fusce fermentum odio nec arcu. Pellentesque libero tortor, tincidunt et, tincidunt eget, semper nec, quam. Phasellus blandit leo ut odio.
                        </p>
                    </v-card-text>
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

    @Component({
        components: {
            'show-summary-dialog': ShowSummaryDialog,
        }
    })
export default class StudyHomeView extends Vue {
        topics: Topic[] = [];
        summaryDialog: boolean = false;

        selectedTopics: Topic[] = [];

        async created() {
            this.topics = await RemoteServices.getTopics();
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
