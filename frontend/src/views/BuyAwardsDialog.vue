<template>
    <v-dialog
            :value="dialog"
            @input="$emit('close-buy-awards-dialog', false)"
            @keydown.esc="$emit('close-buy-awards-dialog', false)"
            max-width="25%"
            max-height="80%"
    >
      <v-card>
        <v-card-title class="justify-center">
        <span class="headline">
          {{ 'Buy Awards' }}
        </span>
        </v-card-title>
        <v-card-text>
          {{'Hello there. It seems like you don\'t have any awards on your account. If you have any Achandos to spend check these out! '+'&#x1f600' }}

        </v-card-text>

        <div>
          <v-badge offset-y="62" offset-x="74" content="Platinum"><v-icon class="mx-6" color="cyan" x-large>fas fa-shield-alt</v-icon></v-badge>
          <v-badge offset-y="60" offset-x="59" content="Gold"><v-icon  class="mx-5" color="#f9c700" large>fas fa-star</v-icon></v-badge>
          <v-badge offset-y="60" offset-x="59" content="Silver"><v-icon class="mx-5" color="#AAA9AD" large>fas fa-star-half-alt</v-icon></v-badge>
        </div>
        <v-spacer></v-spacer>
        <div class="px-0 pt-8" >
          <v-card-text @mouseover="hover=true" @mouseleave="hover=false"
                                             style="font-size: medium"
                                             :class="hover ? 'primary--text' : 'black--text'"
                                             @click="shopRedirect">Get some awards by clicking here
          </v-card-text></div>


        <v-card-actions class="pb-5 pt-0 px-5">
          <v-spacer />
          <v-btn
                  class="mt-2 white--text"
                  color="primary"
                  @click="$emit('close-buy-awards-dialog')"
          >Cancel</v-btn
          >
        </v-card-actions>
      </v-card>
    </v-dialog>
</template>



<script lang="ts">
  import { Component, Model, Prop, Vue } from 'vue-property-decorator';
  import Post from '@/models/management/Post';

  @Component
export default class BuyAwardDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Post, required: true }) readonly post!: Post;
  hover: boolean = false;

  async shopRedirect() {
    await this.$router.push({ name: 'shop-home' });
  }

}



</script>

<style>
</style>
