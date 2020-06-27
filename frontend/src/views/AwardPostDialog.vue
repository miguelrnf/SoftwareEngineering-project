<template>
  <v-dialog
    :value="dialog"
    @input="$emit('close-buy-awards-dialog', false)"
    @keydown.esc="$emit('close-buy-awards-dialog', false)"
    max-width="30%"
  >
    <v-card>
      <v-card-title class="justify-center pb-5"
        >Use your available awards</v-card-title
      >

      <v-card-text class="subtitle-1"
        >By clicking on one of the awards you have below, you are awarding this
        post. The awards distinguishes the best posts, so gift the award wisely
        as they cost Achandos.</v-card-text
      >
      <v-card-text class="overline"
        >#SILVER: the post deserves more attention than usual</v-card-text
      >
      <v-card-text class="overline"
        >#GOLD: the post requires to be seen by most of the people as it has
        high significance</v-card-text
      >
      <v-card-text class="overline"
        >#PLATINUM: distinguishes the best posts as it grants the highest status
        of importance and deserves everyones's recognition</v-card-text
      >
      <div>
        <v-badge
          v-if="numSilvers !== 0"
          :content="'x' + numSilvers"
          offset-y="80"
          offset-x="62"
          class="ma-3"
          ><v-icon
            class="mx-6"
            color="#AAA9AD"
            size="50px"
            @click="awardthisPost('SILVER') && $emit('close-buy-awards-dialog')"
            >fas fa-star-half-alt</v-icon
          ></v-badge
        >
        <v-badge
          v-if="numGolds !== 0"
          :content="'x' + numGolds"
          offset-y="80"
          offset-x="61"
          class="ma-3"
          ><v-icon
            class="mx-5"
            color="#f9c700"
            size="50px"
            @click="awardthisPost('GOLD') && $emit('close-buy-awards-dialog')"
            >fas fa-star</v-icon
          ></v-badge
        >
        <v-badge
          v-if="numPlatinums !== 0"
          :content="'x' + numPlatinums"
          offset-y="86"
          offset-x="64"
          class="ma-3"
          ><v-icon
            class="mx-5"
            color="cyan"
            size="65px"
            @click="
              awardthisPost('PLATINUM') && $emit('close-buy-awards-dialog')
            "
            >fas fa-shield-alt</v-icon
          ></v-badge
        >
      </div>
      <v-spacer />

      <v-card-actions class="pt-7 pt-0 px-5">
        <v-spacer />
        <v-btn
          class="mt-2 white--text"
          color="primary"
          @click="$emit('close-buy-awards-dialog')"
          >Back</v-btn
        >
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import Post from '@/models/management/Post';
import { PostAwardItem } from '@/models/management/PostAwardItem';
import RemoteServices from '@/services/RemoteServices';

@Component
export default class AwardPostDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Post, required: true }) readonly post!: Post;
  userAwardsInventory: PostAwardItem[] = [];
  numSilvers: number = 0;
  numGolds: number = 0;
  numPlatinums: number = 0;

  async getAwards() {
    for (let i = 0; i < this.userAwardsInventory.length; i++) {
      if (this.userAwardsInventory[i].type == 'SILVER') {
        this.numSilvers = this.numSilvers + 1;
      }
      if (this.userAwardsInventory[i].type == 'GOLD') {
        this.numGolds = this.numGolds + 1;
      }
      if (this.userAwardsInventory[i].type == 'PLATINUM') {
        this.numPlatinums = this.numPlatinums + 1;
      }
    }
  }

  async updateLoggedUser() {
    await this.$store.dispatch('updateLoggedUser');
  }

  async awardthisPost(type: string) {
    await this.updateLoggedUser();
    this.userAwardsInventory = await RemoteServices.getAwards();
    if (type == 'SILVER') {
      for (let i = 0; i < this.userAwardsInventory.length; i++) {
        if (this.userAwardsInventory[i].type == 'SILVER') {
          let post2 = await RemoteServices.award(
            this.post.id,
            this.userAwardsInventory[i]
          );
          this.numSilvers = this.numSilvers - 1;
          this.post.awards = post2.awards;
          this.userAwardsInventory = await RemoteServices.getAwards();
          break;
        }
      }
    } else if (type == 'GOLD') {
      for (let i = 0; i < this.userAwardsInventory.length; i++) {
        if (this.userAwardsInventory[i].type == 'GOLD') {
          let post2 = await RemoteServices.award(
            this.post.id,
            this.userAwardsInventory[i]
          );
          this.numGolds = this.numGolds - 1;
          this.post.awards = post2.awards;
          this.userAwardsInventory = await RemoteServices.getAwards();
          break;
        }
      }
    } else if (type == 'PLATINUM') {
      for (let i = 0; i < this.userAwardsInventory.length; i++) {
        if (this.userAwardsInventory[i].type == 'PLATINUM') {
          let post2 = await RemoteServices.award(
            this.post.id,
            this.userAwardsInventory[i]
          );
          this.numPlatinums = this.numPlatinums - 1;
          this.post.awards = post2.awards;
          this.userAwardsInventory = await RemoteServices.getAwards();
          break;
        }
      }
    }
  }

  async created() {
    this.userAwardsInventory = await RemoteServices.getAwards();
    await this.getAwards();
  }
}
</script>

<style></style>
