<template>
  <v-dialog v-model="volunteerProfileDialog" max-width="800" persistent>
    <v-card>
      <v-card-title class="headline">New Volunteer Profile</v-card-title>
      
      <v-card-text>
        <!-- Short Bio Section -->
        <div class="mb-4">
          <label class="text-subtitle-2 font-weight-medium">*Short bio</label>
            <v-text-field
              data-cy="volunteerProfileCreationShortBio"
              v-model="volunteerProfile.shortBio"
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
            :items="participations"
            :items-per-page="5"
            show-select
            v-model="volunteerProfile.selectedParticipations"
            class="elevation-1"
            >

            <template v-slot:item.activity_name="{ item }">
              {{ activityName(item) }}
            </template>
            
            <template v-slot:item.institution_name="{ item }">
              {{ institutionName(item) }}
            </template>

            <template v-slot:item.rating="{ item }">
              {{ getMemberRating(item) }}
            </template>

            <template v-slot:item.review="{ item }">
              {{ item.memberReview || '-' }}
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
import Participation from '@/models/participation/Participation';
import VolunteerProfile from '@/models/volunteer/VolunteerProfile';

export default {
  name: 'VolunteerProfileDialog',
  props: {
    value: {
      type: Boolean,
      default: false
    },
    volunteerProfile: {
      type: VolunteerProfile,
      default: () => ({
        bio: '',
        participations: []
      })
    },
    participations: {
      type: Array[Participation],
      default: () => []
    },
    activityName: Function,
    institutionName: Function,
    getMemberRating: Function
  },
  data() {
    return {
      volunteerProfileDialog: this.value,
      headers: [
        { text: '', value: 'icon', sortable: false, width: '50px' },
        { text: 'Activity Name', value: 'activity_name' },
        { text: 'Institution', value: 'institution_name' },
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
    volunteerProfile(newValue) {
      this.volunteerProfile = newValue;
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