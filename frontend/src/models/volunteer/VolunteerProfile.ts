import Volunteer from './Volunteer';
import Participation from '../participation/Participation';

export default class VolunteerProfile {
  id: number | null = null;
  shortBio: string = '';
  numTotalEnrollments: number = 0;
  numTotalParticipations: number = 0;
  numTotalAssessments: number = 0;
  averageRating: number = 0;
  volunteer: Volunteer | null = null;
  selectedParticipations: Participation[] = [];

  constructor(jsonObj?: VolunteerProfile) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.shortBio = jsonObj.shortBio;
      this.numTotalEnrollments = jsonObj.numTotalEnrollments;
      this.numTotalParticipations = jsonObj.numTotalParticipations;
      this.numTotalAssessments = jsonObj.numTotalAssessments;
      this.averageRating = jsonObj.averageRating;
      this.volunteer = jsonObj.volunteer ? new Volunteer(jsonObj.volunteer) : null;
      this.selectedParticipations = jsonObj.selectedParticipations?.map(
        (participation) => new Participation(participation)
      ) || [];
    }
  }

  toString(): string {
    return `VolunteerProfile { id=${this.id}, shortBio='${this.shortBio}', numTotalEnrollments=${this.numTotalEnrollments}, numTotalParticipations=${this.numTotalParticipations}, numTotalAssessments=${this.numTotalAssessments}, averageRating=${this.averageRating}, volunteer=${this.volunteer}, selectedParticipations=${JSON.stringify(this.selectedParticipations)} }`;
  }
}