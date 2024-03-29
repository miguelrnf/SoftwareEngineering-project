import axios from 'axios';
import Store from '@/store';
import Question from '@/models/management/Question';
import { Quiz } from '@/models/management/Quiz';
import Course from '@/models/user/Course';
import StatementCorrectAnswer from '@/models/statement/StatementCorrectAnswer';
import StudentStats from '@/models/statement/StudentStats';
import StatementQuiz from '@/models/statement/StatementQuiz';
import SolvedQuiz from '@/models/statement/SolvedQuiz';
import Topic from '@/models/management/Topic';
import Assessment from '@/models/management/Assessment';
import AuthDto from '@/models/user/AuthDto';
import StatementAnswer from '@/models/statement/StatementAnswer';
import { QuizAnswers } from '@/models/management/QuizAnswers';
import Suggestion from '@/models/management/Suggestion';
import { Tournament } from '@/models/management/Tournament';
import Post from '@/models/management/Post';
import ListPost from '@/models/management/ListPost';
import { PostQuestion } from '@/models/management/PostQuestion';
import { PostAnswer } from '@/models/management/PostAnswer';
import { PostComment } from '@/models/management/PostComment';
import User from '@/models/user/User';
import ListByUsernameDto from '@/models/management/ListByUsernameDto';
import StatementQuestion from '@/models/statement/StatementQuestion';
import { Student } from '@/models/management/Student';
import { ShopItem } from '@/models/management/ShopItem';
import { Leaderboards } from '@/models/management/Leaderboards';
import { Theme } from '@/models/management/Theme';
import { PostAwardItem } from '@/models/management/PostAwardItem';
import { AwardsPerPost } from '@/models/management/AwardsPerPost';
import Classroom from '@/models/management/Classroom';
import Document from '@/models/management/Document';
import EvalSettings from '@/models/management/EvalSettings';

const httpClient = axios.create();
httpClient.defaults.timeout = 10000;
httpClient.defaults.baseURL = process.env.VUE_APP_ROOT_API;
httpClient.defaults.headers.post['Content-Type'] = 'application/json';
httpClient.interceptors.request.use(
  config => {
    if (!config.headers.Authorization) {
      const token = Store.getters.getToken;

      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }
    }

    return config;
  },
  error => Promise.reject(error)
);

export default class RemoteServices {
  static async fenixLogin(code: string): Promise<AuthDto> {
    return httpClient
      .get(`/auth/fenix?code=${code}`)
      .then(response => {
        return new AuthDto(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async demoStudentLogin(): Promise<AuthDto> {
    return httpClient
      .get('/auth/demo/student')
      .then(response => {
        return new AuthDto(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async demoTeacherLogin(): Promise<AuthDto> {
    return httpClient
      .get('/auth/demo/teacher')
      .then(response => {
        return new AuthDto(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async demoAdminLogin(): Promise<AuthDto> {
    return httpClient
      .get('/auth/demo/admin')
      .then(response => {
        return new AuthDto(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getUserStats(): Promise<StudentStats> {
    return httpClient
      .get(
        `/executions/${Store.getters.getCurrentCourse.courseExecutionId}/stats`
      )
      .then(response => {
        return new StudentStats(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getQuizType(statementQuiz: StatementQuiz): Promise<boolean> {
    return httpClient
      .get(`/quizzes/${statementQuiz.id}/type`)
      .then(response => {
        return response.data as boolean;
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getQuestions(): Promise<Question[]> {
    return httpClient
      .get(`/courses/${Store.getters.getCurrentCourse.courseId}/questions`)
      .then(response => {
        return response.data.map((question: any) => {
          return new Question(question);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getSuggestions(): Promise<Suggestion[]> {
    return httpClient
      .get(
        `/courses/${Store.getters.getCurrentCourse.courseExecutionId}/suggestions/listall`
      )
      .then(response => {
        return response.data.map((suggestion: any) => {
          return new Suggestion(suggestion);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getSuggestionsbyUsername(
    username: string
  ): Promise<ListByUsernameDto> {
    return httpClient
      .get(
        `/courses/${Store.getters.getCurrentCourse.courseExecutionId}/suggestions/listallbyusername/${username}`
      )
      .then(response => {
        return new ListByUsernameDto(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async exportCourseQuestions(): Promise<Blob> {
    return httpClient
      .get(
        `/courses/${Store.getters.getCurrentCourse.courseId}/questions/export`,
        {
          responseType: 'blob'
        }
      )
      .then(response => {
        return new Blob([response.data], {
          type: 'application/zip, application/octet-stream'
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getAvailableQuestions(): Promise<Question[]> {
    return httpClient
      .get(
        `/courses/${Store.getters.getCurrentCourse.courseId}/questions/available`
      )
      .then(response => {
        return response.data.map((question: any) => {
          return new Question(question);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async createQuestion(question: Question): Promise<Question> {
    return httpClient
      .post(
        `/courses/${Store.getters.getCurrentCourse.courseId}/questions/`,
        question
      )
      .then(response => {
        return new Question(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static createSuggestion(sugg: Suggestion): Promise<Suggestion> {
    return httpClient
      .post(
        `/courses/${Store.getters.getCurrentCourse.courseExecutionId}/suggestions/`,
        sugg
      )
      .then(response => {
        return new Suggestion(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static addQuestion(sugg: Suggestion): Promise<Question> {
    return httpClient
      .post(
        `/courses/${Store.getters.getCurrentCourse.courseId}/suggestions/newquestion`,
        sugg
      )
      .then(response => {
        return new Question(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async updateQuestion(question: Question): Promise<Question> {
    return httpClient
      .put(`/questions/${question.id}`, question)
      .then(response => {
        return new Question(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  //--------------------------------------Study remote services --------------------------

  static getTopicQuestions(topicName: String): Promise<Question[]> {
    return httpClient
      .get(
        `/courses/${Store.getters.getCurrentCourse.courseId}/study/getTopicQuestions/${topicName}`
      )
      .then(response => {
        return response.data.map((question: any) => {
          return new Question(question);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getSuggestTopicQuiz(): Promise<string> {
    return httpClient
      .get(
        `/courses/${Store.getters.getCurrentCourse.courseExecutionId}/study/getSuggestedTopic`,
        {}
      )
      .then(response => {
        return response.data as string;
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getMyOwnTopicQuizzes(): Promise<SolvedQuiz[]> {
    return httpClient
      .get(
        `/courses/${Store.getters.getCurrentCourse.courseExecutionId}/study/getMyOwnQuizzes`
      )
      .then(response => {
        return response.data.map((solvedQuiz: any) => {
          return new SolvedQuiz(solvedQuiz);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getAvailableTopics(): Promise<Topic[]> {
    return httpClient
      .get(
        `/courses/${Store.getters.getCurrentCourse.courseExecutionId}/study/getAvailableTopics`
      )
      .then(response => {
        return response.data.map((topic: any) => {
          return new Topic(topic);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async generateTopicStatementQuiz(
    params: object,
    topicName: String
  ): Promise<StatementQuiz> {
    return httpClient
      .post(
        `/courses/${Store.getters.getCurrentCourse.courseExecutionId}/study/newTopicQuiz/${topicName}`,
        params
      )
      .then(response => {
        return new StatementQuiz(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  //--------------------------classroom Remote services ------------------

  static async createClassroom(params: Classroom): Promise<Classroom> {
    return httpClient
      .post(
        `/courses/${Store.getters.getCurrentCourse.courseExecutionId}/classroom/create`,
        params
      )
      .then(response => {
        return new Classroom(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async editClassroom(params: Classroom): Promise<Classroom> {
    return httpClient
      .put(
        `/courses/${Store.getters.getCurrentCourse.courseExecutionId}/classroom/edit`,
        params
      )
      .then(response => {
        return new Classroom(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async addQuizzes(params: Classroom): Promise<Classroom> {
    return httpClient
      .put(
        `/courses/${Store.getters.getCurrentCourse.courseExecutionId}/classroom/addQuizzes`,
        params
      )
      .then(response => {
        return new Classroom(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async changeClassroomStatus(params: Classroom): Promise<Classroom> {
    return httpClient
      .put(
        `/courses/${Store.getters.getCurrentCourse.courseExecutionId}/classroom/changeStatus`,
        params
      )
      .then(response => {
        return new Classroom(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async setEvaluation(
    params: Classroom,
    quiz: StatementQuiz
  ): Promise<Classroom> {
    return httpClient
      .put(
        `/courses/${Store.getters.getCurrentCourse.courseExecutionId}/classroom/${params.id}/${quiz.id}/${quiz.evaluation}`
      )
      .then(response => {
        return new Classroom(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async createDocument(params: Document): Promise<Document> {
    return httpClient
      .post(
        `/courses/${Store.getters.getCurrentCourse.courseExecutionId}/classroom/newDoc`,
        params
      )
      .then(response => {
        return new Document(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async uploadDoc(
    formData: FormData,
    document: Document
  ): Promise<Object> {
    httpClient.defaults.headers.post['Content-Type'] =
      'application/octet-stream';
    return httpClient
      .post(
        `/courses/${Store.getters.getCurrentCourse.courseExecutionId}/classroom/newFile/${document.id}`,
        formData.get('photos')
      )
      .then(response => {
        return new Object(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getDoc(document: Document): Promise<Object> {
    httpClient.defaults.headers.post['Content-Type'] =
      'application/octet-stream';

    return httpClient
      .get(
        `/courses/${Store.getters.getCurrentCourse.courseExecutionId}/classroom/getFile/${document.id}`
      )
      .then(response => {
        return new Object(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async uploadDoc2(formData: FormData, document: Document) {
    return axios({
      method: 'post',
      url: `http://localhost:8080/courses/${Store.getters.getCurrentCourse.courseExecutionId}/classroom/newFile`,
      data: document
      //data: formData,
      //headers: {'Content-Type': 'multipart/form-data' }
    })
      .then(function(response) {
        //handle success
      })
      .catch(function(response) {
        //handle error
      });
  }

  static async editDocument(params: Document): Promise<Document> {
    return httpClient
      .put(
        `/courses/${Store.getters.getCurrentCourse.courseExecutionId}/classroom/editDoc`,
        params
      )
      .then(response => {
        return new Document(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async changeEvalSettings(params: EvalSettings): Promise<EvalSettings> {
    return httpClient
      .put(
        `/courses/${Store.getters.getCurrentCourse.courseExecutionId}/classroom/evalSettings`,
        params
      )
      .then(response => {
        return new EvalSettings(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }
  static async getEvalSettings(): Promise<EvalSettings> {
    return httpClient
      .get(
        `/courses/${Store.getters.getCurrentCourse.courseExecutionId}/classroom/getEvalSettings`
      )
      .then(response => {
        return new EvalSettings(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async deleteClassroom(classroomId: number): Promise<Classroom> {
    return httpClient
      .delete(
        `courses/${Store.getters.getCurrentCourse.courseExecutionId}/classroom/delete/${classroomId}`
      )
      .then(response => {
        return new Classroom(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async deleteClassroomQuiz(
    classroomId: number,
    quizId: number
  ): Promise<Classroom> {
    return httpClient
      .delete(
        `courses/${Store.getters.getCurrentCourse.courseExecutionId}/classroom/deleteQuiz/${classroomId}/${quizId}`
      )
      .then(response => {
        return new Classroom(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async deleteDocument(
    classroomId: number,
    documentId: number
  ): Promise<Document> {
    return httpClient
      .delete(
        `courses/${Store.getters.getCurrentCourse.courseExecutionId}/classroom/deleteDoc/${classroomId}/${documentId}`
      )
      .then(response => {
        return new Document(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getClassrooms(type: String): Promise<Classroom[]> {
    return httpClient
      .get(
        `/courses/${Store.getters.getCurrentCourse.courseExecutionId}/classroom/list/${type}`
      )
      .then(response => {
        return response.data.map((classroom: any) => {
          return new Classroom(classroom);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getClassroomQuizzes(
    classroom: Classroom
  ): Promise<StatementQuiz[]> {
    return httpClient
      .get(
        `/courses/${Store.getters.getCurrentCourse.courseExecutionId}/classroom/listQuizzes/${classroom.id}`
      )
      .then(response => {
        return response.data.map((x: any) => {
          return new StatementQuiz(x);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getClassroomSolvedQuizzes(
    classroom: Classroom
  ): Promise<SolvedQuiz[]> {
    return httpClient
      .get(
        `/courses/${Store.getters.getCurrentCourse.courseExecutionId}/classroom/listSolvedQuizzes/${classroom.id}`
      )
      .then(response => {
        return response.data.map((x: any) => {
          return new SolvedQuiz(x);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getEvalStudents(): Promise<Student[]> {
    return httpClient
      .get(
        `/executions/${Store.getters.getCurrentCourse.courseExecutionId}/eval`
      )
      .then(response => {
        return response.data.map((x: any) => {
          return new Student(x);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  //----------------------------------------------------
  static updateSuggestion(sugg: Suggestion): Promise<Suggestion> {
    return httpClient
      .put(
        `/courses/${Store.getters.getCurrentCourse.courseExecutionId}/suggestions/edit`,
        sugg
      )
      .then(response => {
        return new Suggestion(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static approveSuggestion(sugg: Suggestion): Promise<Suggestion> {
    return httpClient
      .put(
        `/courses/${Store.getters.getCurrentCourse.courseExecutionId}/suggestions/approve`,
        sugg
      )
      .then(response => {
        return new Suggestion(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static setCheckMark(sugg: Suggestion): Promise<Suggestion> {
    return httpClient
      .put(
        `/courses/${Store.getters.getCurrentCourse.courseExecutionId}/suggestions/setCheckMark`,
        sugg
      )
      .then(response => {
        return new Suggestion(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async deleteQuestion(questionId: number) {
    return httpClient.delete(`/questions/${questionId}`).catch(async error => {
      throw Error(await this.errorMessage(error));
    });
  }

  static async deleteSuggestion(suggestionId: number): Promise<Suggestion> {
    return httpClient
      .delete(
        `courses/${Store.getters.getCurrentCourse.courseExecutionId}/suggestions/delete/${suggestionId}`
      )
      .then(response => {
        return new Suggestion(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async setQuestionStatus(
    questionId: number,
    status: String
  ): Promise<Question> {
    return httpClient
      .post(`/questions/${questionId}/set-status`, status, {})
      .then(response => {
        return new Question(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async uploadImage(file: File, questionId: number): Promise<string> {
    let formData = new FormData();
    formData.append('file', file);
    return httpClient
      .put(`/questions/${questionId}/image`, formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      })
      .then(response => {
        return response.data as string;
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async updateQuestionTopics(questionId: number, topics: Topic[]) {
    return httpClient.put(`/questions/${questionId}/topics`, topics);
  }

  static async getTopics(): Promise<Topic[]> {
    return httpClient
      .get(`/courses/${Store.getters.getCurrentCourse.courseId}/topics`)
      .then(response => {
        return response.data.map((topic: any) => {
          return new Topic(topic);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getAvailableQuizzes(): Promise<StatementQuiz[]> {
    return httpClient
      .get(
        `/executions/${Store.getters.getCurrentCourse.courseExecutionId}/quizzes/available`
      )
      .then(response => {
        return response.data.map((statementQuiz: any) => {
          return new StatementQuiz(statementQuiz);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async generateStatementQuiz(params: object): Promise<StatementQuiz> {
    return httpClient
      .post(
        `/executions/${Store.getters.getCurrentCourse.courseExecutionId}/quizzes/generate`,
        params
      )
      .then(response => {
        return new StatementQuiz(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getOpenedTournaments(): Promise<Tournament[]> {
    return httpClient
      .get(
        `/executions/${Store.getters.getCurrentCourse.courseExecutionId}/tournaments/open`
      )
      .then(response => {
        return response.data.map((tournament: any) => {
          return new Tournament(tournament);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getAllTournaments(): Promise<Tournament[]> {
    return httpClient
      .get(
        `/executions/${Store.getters.getCurrentCourse.courseExecutionId}/teacher/tournaments`
      )
      .then(response => {
        return response.data.map((tournament: any) => {
          return new Tournament(tournament);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getOwnTournaments(): Promise<Tournament[]> {
    return httpClient
      .get(
        `/executions/${Store.getters.getCurrentCourse.courseExecutionId}/tournaments/own/${Store.getters.getUser.username}`
      )
      .then(response => {
        return response.data.map((tournament: any) => {
          return new Tournament(tournament);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getEnrolledTournaments(): Promise<Tournament[]> {
    return httpClient
      .get(
        `/executions/${Store.getters.getCurrentCourse.courseExecutionId}/tournaments/enrolled/${Store.getters.getUser.username}`
      )
      .then(response => {
        return response.data.map((tournament: any) => {
          return new Tournament(tournament);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getTournaments(): Promise<Tournament[]> {
    return httpClient
      .get('/tournaments')
      .then(response => {
        return response.data.map((tournament: any) => {
          return new Tournament(tournament);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getTournament(
    id: number,
    username: string
  ): Promise<Tournament> {
    return httpClient
      .get(
        `/executions/${Store.getters.getCurrentCourse.courseExecutionId}/tournaments/${id}/${username}`
      )
      .then(response => {
        return new Tournament(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getTournamentsByUser(username: string): Promise<Tournament[]> {
    return httpClient
      .get(
        `executions/${Store.getters.getCurrentCourse.courseExecutionId}/tournaments/user/${username}`
      )
      .then(response => {
        return response.data.map((tournament: any) => {
          return new Tournament(tournament);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getShopItems(): Promise<ShopItem[]> {
    return httpClient
      .get('shop')
      .then(response => {
        return response.data.map((shopItem: any) => {
          return new ShopItem(shopItem);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async enrollTournament(tournamentId: Number): Promise<Tournament> {
    return httpClient
      .put(`/tournament/${tournamentId}/opened/enroll`)
      .then(response => {
        return new Tournament(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async cancelTournament(tournamentId: Number): Promise<Tournament> {
    return httpClient
      .put(`/tournament/${tournamentId}/cancel`)
      .then(response => {
        return new Tournament(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async unenrollTournament(tournamentId: Number): Promise<Tournament> {
    return httpClient
      .put(`/tournament/${tournamentId}/opened/unenroll`)
      .then(response => {
        return new Tournament(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async createNewTournament(
    tournament: Tournament
  ): Promise<Tournament> {
    return httpClient
      .post(
        `/executions/${Store.getters.getCurrentCourse.courseExecutionId}/tournaments`,
        tournament
      )
      .then(response => {
        return new Tournament(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async editTournament(tournament: Tournament): Promise<Tournament> {
    return httpClient
      .put(
        `/executions/${Store.getters.getCurrentCourse.courseExecutionId}/tournaments/edit`,
        tournament
      )
      .then(response => {
        return new Tournament(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getSolvedQuizzes(): Promise<SolvedQuiz[]> {
    return httpClient
      .get(
        `/executions/${Store.getters.getCurrentCourse.courseExecutionId}/quizzes/solved`
      )
      .then(response => {
        return response.data.map((solvedQuiz: any) => {
          return new SolvedQuiz(solvedQuiz);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getQuizByQRCode(quizId: number): Promise<StatementQuiz> {
    return httpClient
      .get(`/quizzes/${quizId}/byqrcode`)
      .then(response => {
        return new StatementQuiz(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async exportQuiz(quizId: number): Promise<Blob> {
    return httpClient
      .get(`/quizzes/${quizId}/export`, {
        responseType: 'blob'
      })
      .then(response => {
        return new Blob([response.data], {
          type: 'application/zip, application/octet-stream'
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async startQuiz(quizId: number) {
    return httpClient.get(`/quizzes/${quizId}/start`).catch(async error => {
      throw Error(await this.errorMessage(error));
    });
  }

  static async submitAnswer(quizId: number, answer: StatementAnswer) {
    return httpClient
      .post(`/quizzes/${quizId}/submit`, answer)
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async concludeQuiz(
    quizId: number
  ): Promise<StatementCorrectAnswer[] | void> {
    return httpClient
      .get(`/quizzes/${quizId}/conclude`)
      .then(response => {
        if (response.data) {
          return response.data.map((answer: any) => {
            return new StatementCorrectAnswer(answer);
          });
        }
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async createTopic(topic: Topic): Promise<Topic> {
    return httpClient
      .post(
        `/courses/${Store.getters.getCurrentCourse.courseId}/topics/`,
        topic
      )
      .then(response => {
        return new Topic(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async updateTopic(topic: Topic): Promise<Topic> {
    return httpClient
      .put(`/topics/${topic.id}`, topic)
      .then(response => {
        return new Topic(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async deleteTopic(topic: Topic) {
    return httpClient.delete(`/topics/${topic.id}`).catch(async error => {
      throw Error(await this.errorMessage(error));
    });
  }

  static async getNonGeneratedQuizzes(): Promise<Quiz[]> {
    return httpClient
      .get(
        `/executions/${Store.getters.getCurrentCourse.courseExecutionId}/quizzes/non-generated`
      )
      .then(response => {
        return response.data.map((quiz: any) => {
          return new Quiz(quiz);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async deleteQuiz(quizId: number) {
    return httpClient.delete(`/quizzes/${quizId}`).catch(async error => {
      throw Error(await this.errorMessage(error));
    });
  }

  static async getQuiz(quizId: number): Promise<Quiz> {
    return httpClient
      .get(`/quizzes/${quizId}`)
      .then(response => {
        return new Quiz(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getQuizAnswers(quizId: number): Promise<QuizAnswers> {
    return httpClient
      .get(`/quizzes/${quizId}/answers`)
      .then(response => {
        return new QuizAnswers(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async saveQuiz(quiz: Quiz): Promise<Quiz> {
    if (quiz.id) {
      return httpClient
        .put(`/quizzes/${quiz.id}`, quiz)
        .then(response => {
          return new Quiz(response.data);
        })
        .catch(async error => {
          throw Error(await this.errorMessage(error));
        });
    } else {
      return httpClient
        .post(
          `/executions/${Store.getters.getCurrentCourse.courseExecutionId}/quizzes`,
          quiz
        )
        .then(response => {
          return new Quiz(response.data);
        })
        .catch(async error => {
          throw Error(await this.errorMessage(error));
        });
    }
  }

  static async getCourseStudents(course: Course, user: Boolean) {
    return httpClient
      .get(`/executions/${course.courseExecutionId}/students`)
      .then(response => {
        return response.data.map((student: any) => {
          if (user) {
            return new User(student);
          } else {
            return new Student(student);
          }
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getAssessments(): Promise<Assessment[]> {
    return httpClient
      .get(
        `/executions/${Store.getters.getCurrentCourse.courseExecutionId}/assessments`
      )
      .then(response => {
        return response.data.map((assessment: any) => {
          return new Assessment(assessment);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getAvailableAssessments() {
    return httpClient
      .get(
        `/executions/${Store.getters.getCurrentCourse.courseExecutionId}/assessments/available`
      )
      .then(response => {
        return response.data.map((assessment: any) => {
          return new Assessment(assessment);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async updateCurrentTheme(themeName: string): Promise<User> {
    return httpClient
      .get(`/users/update/theme/${themeName}`)
      .then(response => {
        return new User(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getCurrentTheme(): Promise<Theme> {
    return httpClient
      .get('/users/currentTheme')
      .then(response => {
        return new Theme(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getOwnedThemes(): Promise<string[]> {
    return httpClient
      .get('/users/themes/owned')
      .then(response => {
        return response.data;
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getThemes(): Promise<Theme[]> {
    return httpClient
      .get('/users/themes')
      .then(response => {
        return response.data.map((theme: any) => {
          return new Theme(theme);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async updateUser(): Promise<User> {
    return httpClient
      .get('/users/update')
      .then(response => {
        return new User(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async saveAssessment(assessment: Assessment) {
    if (assessment.id) {
      return httpClient
        .put(`/assessments/${assessment.id}`, assessment)
        .then(response => {
          return new Assessment(response.data);
        })
        .catch(async error => {
          throw Error(await this.errorMessage(error));
        });
    } else {
      return httpClient
        .post(
          `/executions/${Store.getters.getCurrentCourse.courseExecutionId}/assessments`,
          assessment
        )
        .then(response => {
          return new Assessment(response.data);
        })
        .catch(async error => {
          throw Error(await this.errorMessage(error));
        });
    }
  }

  static async deleteAssessment(assessmentId: number) {
    return httpClient
      .delete(`/assessments/${assessmentId}`)
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async setAssessmentStatus(
    assessmentId: number,
    status: string
  ): Promise<Assessment> {
    return httpClient
      .post(`/assessments/${assessmentId}/set-status`, status, {
        headers: {
          'Content-Type': 'text/html'
        }
      })
      .then(response => {
        return new Assessment(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static getCourses(): Promise<Course[]> {
    return httpClient
      .get('/courses/executions')
      .then(response => {
        return response.data.map((course: any) => {
          return new Course(course);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async activateCourse(course: Course): Promise<Course> {
    return httpClient
      .post('/courses/activate', course)
      .then(response => {
        return new Course(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async createExternalCourse(course: Course): Promise<Course> {
    return httpClient
      .post('/courses/external', course)
      .then(response => {
        return new Course(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async deleteCourse(courseExecutionId: number | undefined) {
    return httpClient
      .delete(`/executions/${courseExecutionId}`)
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async exportAll() {
    return httpClient
      .get('/admin/export', {
        responseType: 'blob'
      })
      .then(response => {
        const url = window.URL.createObjectURL(new Blob([response.data]));
        const link = document.createElement('a');
        link.href = url;
        let dateTime = new Date();
        link.setAttribute(
          'download',
          `export-${dateTime.toLocaleString()}.zip`
        );
        document.body.appendChild(link);
        link.click();
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async viewPosts(perPage: number, page: number): Promise<ListPost> {
    return httpClient
      .get(
        `executions/${Store.getters.getCurrentCourse.courseExecutionId}/posts/${perPage}/${page}`
      )
      .then(response => {
        return new ListPost(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async submitPost(postQ: PostQuestion): Promise<Post> {
    return httpClient
      .post(
        `executions/${Store.getters.getCurrentCourse.courseExecutionId}/posts/submit`,
        postQ
      )
      .then(response => {
        return new Post(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async postAnswer(post: Post): Promise<Post> {
    if (post.answer != null) {
      post.answer.post = new Post();
      post.answer.post.id = post.id;
    }
    return httpClient
      .post(
        `executions/${Store.getters.getCurrentCourse.courseExecutionId}/posts/${post.id}/answer`,
        post.answer
      )
      .then(response => {
        return new Post(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getPost(id: number): Promise<Post> {
    return httpClient
      .get(
        `executions/${Store.getters.getCurrentCourse.courseExecutionId}/posts/${id}`
      )
      .then(response => {
        return new Post(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async updatePost(post: Post): Promise<Post> {
    return httpClient
      .put(
        `executions/${Store.getters.getCurrentCourse.courseExecutionId}/posts/${post.id}/edit`,
        post.question as PostQuestion
      )
      .then(response => {
        return new Post(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async removeTwoOptions(
    statementQuestion: StatementQuestion,
    quizId: Number
  ): Promise<StatementQuestion> {
    return httpClient
      .put(
        `tournaments/fiftyFifty/${quizId}`,
        statementQuestion as StatementQuestion
      )
      .then(response => {
        return new StatementQuestion(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async rigthAnswer(
    statementQuestion: StatementQuestion,
    quizId: Number
  ): Promise<StatementQuestion> {
    return httpClient
      .put(
        `tournaments/rigthAnswer/${quizId}`,
        statementQuestion as StatementQuestion
      )
      .then(response => {
        return new StatementQuestion(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getHint(
    statementQuestion: StatementQuestion,
    quizId: Number
  ): Promise<string> {
    return httpClient
      .put(
        `tournaments/getHint/${quizId}`,
        statementQuestion as StatementQuestion
      )
      .then(response => {
        return response.data as string;
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getNumOfPowerUp(type: String): Promise<number> {
    return httpClient
      .get(`/users/numberPowerUps/${type}`)
      .then(response => {
        return response.data as number;
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async changeDiscussStatus(id: number): Promise<Post> {
    return httpClient
      .put(
        `executions/${Store.getters.getCurrentCourse.courseExecutionId}/posts/${id}/edit/discuss`
      )
      .then(response => {
        return new Post(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async changePostStatus(id: number): Promise<Post> {
    return httpClient
      .put(
        `executions/${Store.getters.getCurrentCourse.courseExecutionId}/posts/${id}/edit/status`
      )
      .then(response => {
        return new Post(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async createShopTheme(theme: ShopItem) {
    return httpClient
      .post('shop/add', theme)
      .then(response => {
        return new ShopItem(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async buyItem(itemId: number) {
    return httpClient
      .put(`shop/buy/${itemId}`)
      .then(response => {
        return new ShopItem(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async changeDashboardPrivacy(): Promise<User> {
    return httpClient
      .put('/users/dashboard/privacy')
      .then(response => {
        return new User(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async deletePost(id: number): Promise<Post> {
    return httpClient
      .delete(
        `executions/${Store.getters.getCurrentCourse.courseExecutionId}/posts/${id}`
      )
      .then(response => {
        return new Post(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async updateAnswer(postA: PostAnswer): Promise<Post> {
    return httpClient
      .put(
        `executions/${Store.getters.getCurrentCourse.courseExecutionId}/posts/${postA.post.id}/answer/edit`,
        postA
      )
      .then(response => {
        return new Post(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async writeComment(postC: PostComment): Promise<Post> {
    return httpClient
      .put(
        `executions/${Store.getters.getCurrentCourse.courseExecutionId}/posts/${postC.post.id}/comment`,
        postC
      )
      .then(response => {
        return new Post(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getPostsByUser(username: string): Promise<ListPost> {
    return httpClient
      .get(
        `executions/${Store.getters.getCurrentCourse.courseExecutionId}/posts/user/${username}`
      )
      .then(response => {
        return new ListPost(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async changePostPrivacy(id: number): Promise<Post> {
    return httpClient
      .put(
        `executions/${Store.getters.getCurrentCourse.courseExecutionId}/posts/${id}/edit/privacy`
      )
      .then(response => {
        return new Post(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async changeAnswerPrivacy(id: number): Promise<Post> {
    return httpClient
      .put(
        `executions/${Store.getters.getCurrentCourse.courseExecutionId}/posts/${id}/answer/edit/privacy`
      )
      .then(response => {
        return new Post(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async postsByQuiz(quizid: number): Promise<ListPost> {
    return httpClient
      .get(
        `executions/${Store.getters.getCurrentCourse.courseExecutionId}/posts/quiz/${quizid}`
      )
      .then(response => {
        return new ListPost(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getLeaderboards(): Promise<Leaderboards> {
    return httpClient
      .get(
        `executions/${Store.getters.getCurrentCourse.courseExecutionId}/leaderboards`
      )
      .then(response => {
        return new Leaderboards(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async vote(id: number, vote: string): Promise<Post> {
    return httpClient
      .put(
        `executions/${Store.getters.getCurrentCourse.courseExecutionId}/posts/${id}/${vote}`
      )
      .then(response => {
        return new Post(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async updateLoggedUser(): Promise<User> {
    return httpClient
      .get('/users/update')
      .then(response => {
        return new User(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async award(id: number, award: PostAwardItem): Promise<Post> {
    return httpClient
      .put(
        `executions/${Store.getters.getCurrentCourse.courseExecutionId}/posts/${id}/award`,
        award
      )
      .then(response => {
        return new Post(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getAwards(): Promise<PostAwardItem[]> {
    return httpClient
      .get('/users/updateAwards')
      .then(response => {
        return response.data.map((awards: any) => {
          return new PostAwardItem(awards);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getAwardsOnThisPost(id: number): Promise<AwardsPerPost[]> {
    return httpClient
      .get(
        `executions/${Store.getters.getCurrentCourse.courseExecutionId}/posts/${id}/getAwards`
      )
      .then(response => {
        return response.data.map((awards: any) => {
          return new AwardsPerPost(awards);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async errorMessage(error: any): Promise<string> {
    if (error.message === 'Network Error') {
      return 'Unable to connect to server';
    } else if (error.message.split(' ')[0] === 'timeout') {
      return 'Request timeout - Server took too long to respond';
    } else if (error.response) {
      return error.response.data.message;
    } else if (error.message === 'Request failed with status code 403') {
      await Store.dispatch('logout');
      return 'Unauthorized access or Expired token';
    } else {
      return 'Unknown Error - Contact admin';
    }
  }
}
