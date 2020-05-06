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
        <span class="headline">
          {{
            'New Question'
          }}
        </span>
            </v-card-title>

            <v-card-text class="text-left" v-if="currentSuggestion">
                <v-container grid-list-md fluid>
                    <v-layout column wrap>
                        <v-flex xs24 sm12 md8>
                          <v-text-field v-model="currentSuggestion.title" label="Title" />
                        </v-flex>
                        <v-flex xs24 sm12 md12>
                            <v-textarea
                                    outline
                                    rows="10"
                                    v-model="currentSuggestion._questionStr"
                                    label="Content"
                                    outlined
                                    data-cy="content"
                            ></v-textarea>
                        </v-flex>

                        <v-flex
                          xs24
                          sm12
                          md12
                          v-for="index in currentSuggestion.options.length"
                          :key="index"
                        >
                          <v-switch
                            v-model="currentSuggestion.options[index - 1].correct"
                            class="ma-4"
                            label="Correct"
                          />
                          <v-textarea
                            outline
                            rows="10"
                            v-model="currentSuggestion.options[index - 1].content"
                            :label="`Option ${index}`"
                          ></v-textarea>
                        </v-flex>
                    </v-layout>
                </v-container>
            </v-card-text>
            <v-card-text>
                <v-autocomplete
                        v-model="questionTopics"
                        :items="questionTopics"
                        multiple
                        return-object
                        item-text="name"
                        item-value="name"
                        label="Topics"
                        outlined
                        @change="saveTopics"
                        data-cy="topics"
                >
                    <template v-slot:selection="data">
                        <v-chip
                                v-bind="data.attrs"
                                :input-value="data.selected"
                                close
                                @click="data.select"
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
            </v-card-text>
            <v-card-actions>
                <v-spacer />
                <v-btn
                        color="blue darken-1"
                        @click="$emit('dialog', false)"
                        data-cy="cancel"
                >Cancel</v-btn
                >
                <v-btn
                        color="blue darken-1"
                        @click="saveSuggestion"
                        data-cy="saveButton"
                >Save</v-btn
                >
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>

<script lang="ts">
    import { Component, Model, Prop, Vue } from 'vue-property-decorator';
    import Question from '@/models/management/Question';
    import RemoteServices from '@/services/RemoteServices';
    import Suggestion from '@/models/management/Suggestion';
    import Topic from '@/models/management/Topic';
    import Option from '@/models/management/Option';

    @Component
    export default class AddQuestionDialog extends Vue {
        @Model('dialog', Boolean) dialog!: boolean;
        @Prop({ type: Suggestion, required: true }) readonly suggestion!: Suggestion;

        currentSuggestion!: Suggestion;

        questionTopics: Topic[] = JSON.parse(
            JSON.stringify(this.suggestion._topicsList)
        );

        created() {
            /*if (
                    this.suggestion &&
                    (this.suggestion.status!='REJECTED')
            ) {
              this.$store.dispatch(
                      'error',
                      'You can only edit a rejected suggestion'
              );
              return;
            }*/

            this.currentSuggestion = new Suggestion(this.suggestion);
            if( this.currentSuggestion.options.length !=4 ) this.currentSuggestion.options = [new Option(), new Option(), new Option(), new Option()];
            console.log(this.currentSuggestion);
        }

        // TODO use EasyMDE with these configs
        // markdownConfigs: object = {
        //   status: false,
        //   spellChecker: false,
        //   insertTexts: {
        //     image: ['![image][image]', '']
        //   }
        // };

        async saveSuggestion() {
            if (this.currentSuggestion && !this.currentSuggestion.title) {
                await this.$store.dispatch('error', 'New Question must have title');
                return;
            }

            if (this.currentSuggestion) {
                try {
                    const result = await RemoteServices.addQuestion(this.currentSuggestion);
                    this.$emit('save-suggestion', result);
                } catch (error) {
                    await this.$store.dispatch('error', error);
                }
            }
        }

        async saveTopics() {
            if (this.suggestion._id) {
                try {
                    this.suggestion._topicsList = this.questionTopics;
                } catch (error) {
                    await this.$store.dispatch('error', error);
                }
            }

            this.$emit(
                'question-changed-topics',
                this.suggestion._id,
                this.questionTopics
            );
        }

        removeTopic(topic: Topic) {
            this.questionTopics = this.questionTopics.filter(
                element => element.id != topic.id
            );
            this.saveTopics();
        }
    }
</script>
