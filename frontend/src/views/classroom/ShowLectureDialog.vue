<template>
  <v-dialog
    :value="dialog"
    @input="closeLectureDialog"
    @keydown.esc="closeLectureDialog"
    max-width="80%"

  >
    <v-card  max-height="85%" class="px-5">
        <v-row>

          <v-card-title color="primary" class="mb-5 table" >
            <v-icon left>{{getLectureTypeIcon()}}</v-icon>
            {{getLectureTypeCaps()}}
          </v-card-title>

        </v-row>
        <v-tabs >
            <v-tab @click="setTabName('New Document')">
                <v-icon left>far fa-bookmark</v-icon>
                DOCUMENTATION
            </v-tab >
            <v-tab @click="setTabName('New Video')">
                <v-icon left>fas fa-play</v-icon>
                VIDEOS
            </v-tab>
            <v-spacer></v-spacer>
            <v-btn color="primary" class="mr-6" v-if="teacher" @click="newDocument">{{this.doctype}}</v-btn>

        </v-tabs>







      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn color="primary" text @click="closeLectureDialog">
          Close
        </v-btn>
      </v-card-actions>
        <edit-document-dialog
                v-if="current && newOrEditDialog"
                v-model="newOrEditDialog"
                :document="current"
                :type="doctype"
                :dialog="newOrEditDialog"
                v-on:save-document="onSaveDocument"
        />
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import Suggestion from '@/models/management/Suggestion';
import RemoteServices from '@/services/RemoteServices';
import Image from '@/models/management/Image';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import ShowSuggestion from '@/views/ShowSuggestion.vue';
import Classroom from '@/models/management/Classroom';
import Document from '@/models/management/Document';
import EditDocumentDialog from '@/views/classroom/EditDocumentDialog.vue';

@Component({
  components: {
    'edit-document-dialog': EditDocumentDialog
  }
})
export default class ShowLectureDialog extends Vue {
  @Prop({ type: Classroom, required: true }) readonly lecture!: Classroom;
  @Prop({ type: Boolean, required: true }) readonly dialog!: boolean;
  @Prop({ type: String, required: true }) readonly type!: String;
  @Prop({ type: Boolean, required: true }) readonly teacher!: boolean;




  doctype: string = 'New Document';

  videos: Document[] = [];
  documents: Document[] = [];

  newOrEditDialog: boolean = false;
  current: Document | null = null;

  async created() {
    this.videos = this.lecture.documents.filter(d => d.type != 'VIDEO')
    this.documents = this.lecture.documents.filter(d => d.type != 'DOC')

  }

  closeLectureDialog() {
    this.$emit('close-show-lecture-dialog');
  }

  getLectureType() {
    if (this.type === 'New Lecture') {
      return 'Lecture'
    } else if (this.type === 'New Lab') {
      return 'Lab'
    } else {
      return 'Project'
    }
  }

  getLectureTypeCaps() {
    if (this.type === 'New Lecture') {
      return 'LECTURE'
    } else if (this.type === 'New Lab') {
      return 'LAB'
    } else {
      return 'PROJECT'
    }
  }

  newDocument() {
    this.current = new Document();
    this.newOrEditDialog = true;
  }

  async onSaveDocument(doc: Document) {

    if(this.doctype === 'New Document'){
      this.documents = this.documents.filter(d => d.id != doc.id);
      this.documents.unshift(doc);
    }
    else{
      this.videos = this.videos.filter(v => v.id != doc.id);
      this.videos.unshift(doc);
    }

    this.newOrEditDialog = false;
    this.current = null;
  }

  getLectureTypeIcon(){
    if (this.type === 'New Lecture') {
      return 'fab fa-leanpub'
    } else if (this.type === 'New Lab') {
      return 'fas fa-laptop-code'
    } else {
      return 'fab fa-git-alt'
    }
  }
  setTabName(str: string){
    this.doctype = str;
  }

  getColor2(type: string) {
    if (type == 'ACTIVE') return 'success';
    else if (type == 'INACTIVE') return 'warning';

  }


  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }


}
</script>
