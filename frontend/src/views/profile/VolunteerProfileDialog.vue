<template>
  <v-dialog v-model="volunteerProfileDialog" max-width="800" persistent>
    <v-card>
      <v-card-title class="headline">New Volunteer Profile</v-card-title>
      
      <v-card-text>
        <!-- Short Bio Section -->
        <div class="mb-4">
          <label class="text-subtitle-2 font-weight-medium">*Short bio</label>
            <v-text-field
              v-model="volunteer.shortBio"
              outlined
              dense
              placeholder="Enter a short bio"
            ></v-text-field>
            </div>
        
        <v-divider class="my-4"></v-divider>
        
        <!-- Selected Participations Section -->
        <div>
          <h3 class="subtitle-1 font-weight-bold mb-4">Selected Participations</h3>
          
          <v-data-table
            :headers="headers"
            :items="volunteer.participations"
            :items-per-page="5"
            show-select
            class="elevation-1"
          >
            <template v-slot:item.rating="{ item }">
              <v-rating
                v-if="item.rating"
                :value="parseInt(item.rating)"
                color="amber"
                dense
                half-increments
                readonly
                size="18"
              ></v-rating>
              <span v-else>-</span>
            </template>
            
            <template v-slot:item.review="{ item }">
              {{ item.review || '-' }}
            </template>
          </v-data-table>
        </div>
      </v-card-text>
      
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn color="grey darken-3" text @click="onClose">CLOSE</v-btn>
        <v-btn color="grey darken-3" text @click="onSave">SAVE</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>
export default {
  name: 'VolunteerProfileDialog',
  props: {
    value: {
      type: Boolean,
      default: false
    },
    volunteer: {
      type: Object,
      default: () => ({
        bio: '',
        participations: []
      })
    }
  },
  data() {
    return {
      volunteerProfileDialog: this.value,
      headers: [
        { text: '', value: 'icon', sortable: false, width: '50px' },
        { text: 'Activity Name', value: 'activityName' },
        { text: 'Institution', value: 'institution' },
        { text: 'Rating', value: 'rating' },
        { text: 'Review', value: 'review' },
        { text: 'Acceptance Date', value: 'acceptanceDate' }
      ]
    }
  },
  watch: {
    value(newValue) {
      this.volunteerProfileDialog = newValue;
    },
    volunteer(newValue) {
      this.volunteer = newValue;
    }
  },
  computed: {
    isDialogOpen() {
      return this.volunteerProfileDialog;
    }
  },
  mounted() {
    this.volunteerProfileDialog = this.value;
  },
  methods: {
    onClose() {
      this.volunteerProfileDialog = false;
      this.$emit('close-profile-dialog');
    },
    onSave() {
      this.$emit('create-profile-dialog');
    }
  }
}
</script>

<style scoped>
.subtitle-1 {
  color: rgba(0, 0, 0, 0.87);
}
</style>