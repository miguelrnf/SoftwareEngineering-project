<template>
  <v-dialog
    :value="dialog"
    @input="$emit('dialog', false)"
    @keydown.esc="$emit('dialog', false)"
    max-width="75%"



  >
    <v-card class="px-12 ma-0 table"  height="60%">
      <v-row>

        <v-card-title color="primary" class="mb-5 table" >
          <v-icon left>fas fa-plus</v-icon>
          {{getDocumentTypeCaps()}}
        </v-card-title>

      </v-row>
      <v-row>

        <v-textarea v-if="type !== 'New Document'"

                :label=getDocumentLabel()
                v-model="editDocument.title"
                auto-grow
                outlined
                rows="5"
                row-height="15"

        ></v-textarea>
        <v-textarea v-if="type === 'New Document'"

                    :label=getDocumentLabel()
                    v-model="editDocument.title"
                    auto-grow
                    outlined
                    rows="5"
                    row-height="15"

        ></v-textarea>
      </v-row>

      <v-row>

        <v-textarea v-if="type !== 'New Document'"

                    label="Write Video Url Here"
                    v-model="editDocument.url"
                    auto-grow
                    outlined
                    rows="1"
                    row-height="15"

        ></v-textarea>
        <v-textarea v-if="type === 'New Document'"

                    label="Write Document Content Here"
                    v-model="editDocument.content"
                    auto-grow
                    outlined
                    rows="5"
                    row-height="15"

        ></v-textarea>
      </v-row>
      <v-row>



        <v-btn v-if="type !== 'New Document'" text color="primary" @click="previewVideo()">Preview</v-btn>
      </v-row>
        <v-container class="test" >
        <LazyYoutubeVideo
                v-if="preview && type !== 'New Document'"
                :src="previewVideo()"
                preview-image-size=sddefault


        />
        </v-container>




      <v-card-actions>
        <v-spacer />
        <v-btn
          color="primary"
          text
          @click="$emit('dialog', false)"
          data-cy="cancel"
          >Cancel</v-btn
        >
        <v-btn
          color="primary"
          text
          @click="saveDocument"
          data-cy="saveButton"
          >Save</v-btn
        >
      </v-card-actions>


    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Suggestion from '@/models/management/Suggestion';
import Topic from '@/models/management/Topic';
import ToggleButton from 'vue-js-toggle-button';
import User from '@/models/user/User';
import Image from '@/models/management/Image';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import ShowSuggestion from '@/views/ShowSuggestion.vue';
import VueYouTubeEmbed, { getIdFromURL } from 'vue-youtube-embed';
import LazyYoutubeVideo from 'vue-lazy-youtube-video'
import 'vue-lazy-youtube-video/dist/style.css'
import Classroom from '@/models/management/Classroom';
import Document from '@/models/management/Document';


Vue.use(ToggleButton);

@Component({
  components: {
    LazyYoutubeVideo

  },


})
export default class EditDocumentDialog extends Vue {

  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Document, required: true }) readonly document!: Document;
  @Prop({ type: String, required: true }) readonly type!: String;
  @Prop({ type: Classroom, required: true }) readonly lecture!: Classroom;


  editDocument!: Document;

  previewImageSize: string = "sddefault";
  preview : boolean = false;
  videoId : string = '';
  videoBase : string = 'https://www.youtube.com/embed/';




  async created() {
    this.editDocument = new Document(this.document);
    this.videoId = getIdFromURL('https://www.youtube.com/watch?v=KBMO_4Nj4HQ');

  }


  getDocumentTypeCaps() {
    if(this.type === 'New Document'){
      return 'DOCUMENT'
    } else {
      return 'VIDEO'
    }
  }

  async saveDocument() {
    this.editDocument.url = this.previewVideo();

    if (this.editDocument && this.editDocument.id != null) {

      try {

        const result = await RemoteServices.editDocument(
                this.editDocument
        );
        this.$emit('save-document', result);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    } else if (this.editDocument) {



      this.editDocument.classroomId = this.lecture.id;

      if (this.type === 'New Document') {
        this.editDocument.type = 'DOC'
      }
      else {
        this.editDocument.type = 'VIDEO'
      }
      try {

        const result = await RemoteServices.createDocument(
                this.editDocument
        );


        this.$emit('save-document', result);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }


  previewVideo(){
    var tempvar = '';

    this.videoId = getIdFromURL(this.editDocument.url);

    tempvar = this.videoBase;
    tempvar = tempvar + this.videoId;
    this.preview = true;
    return tempvar
  }


  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }

  getDocumentLabel() {
    if (this.type === 'New Document') {
      return 'Write Document Title Here'
    }  else {
      return 'Write Video Title Here'
    }
  }



}
</script>
<style lang="scss" scoped>
  $gap: 20px;

  .test {
    justify-content: center;
    width: 30%;

  }


</style>
