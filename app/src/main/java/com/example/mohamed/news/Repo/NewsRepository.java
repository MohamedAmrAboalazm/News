package com.example.mohamed.news.Repo;

import com.example.mohamed.news.Api.ApiManager;
import com.example.mohamed.news.DataBase.NewsDataBase;
import com.example.mohamed.news.Models.NewsResponse.ArticlesItem;
import com.example.mohamed.news.Models.NewsResponse.NewsResponse;
import com.example.mohamed.news.Models.SourcesResponse.SourcesItem;
import com.example.mohamed.news.Models.SourcesResponse.SourcesResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsRepository {

    String Lang;
   public static final String APIKEY="dc3d106e730c4256b8c275d9da58d090";


    public NewsRepository(String lang) {
        Lang = lang;
    }

   public void getNewsSources(final onSourcesPreparedListener onSourcesPreparedListener){


       ApiManager
               .getApis()
               .getNewsSources(APIKEY  ,Lang)
               .enqueue(new Callback<SourcesResponse>() {
                   @Override
                   public void onResponse(Call<SourcesResponse> call,
                                          Response<SourcesResponse> response) {


                       if(response.isSuccessful()){
                           if(onSourcesPreparedListener!=null){
                           List<SourcesItem> list
                                   = response
                                   .body()
                                   .getSources();
                           onSourcesPreparedListener.onSourcesPrepared(list);

                           InsertSourcesThread insertSourcesThread=new InsertSourcesThread(list);
                           insertSourcesThread.start();
                           }
                       }
                   }

                   @Override
                   public void onFailure(Call<SourcesResponse> call, Throwable t) {


                       RetrieveSourcesThread retrieveSourcesThread =new RetrieveSourcesThread(onSourcesPreparedListener);
                       retrieveSourcesThread.start();
                       //showMessage(getString(R.string.error),t.getLocalizedMessage(),getString(R.string.ok));
                   }
               });
   }

   public  void getNewsBySourceId(final String sourceId, final onNewsPreparedListener onNewsPreparedListener){

       ApiManager
               .getApis()
               .getNewsBySourceId(APIKEY  ,Lang,sourceId)
               .enqueue(new Callback<NewsResponse>() {
                   @Override
                   public void onResponse(Call<NewsResponse> call,
                                          Response<NewsResponse> response) {


                       if(response.isSuccessful()){
                           List<ArticlesItem>newsList=response.body().getArticles();
                           if(onNewsPreparedListener !=null){
                               onNewsPreparedListener.onNewsPrepared(newsList);

                               InsertArticlesThread insertArticlesThread=new InsertArticlesThread(newsList);
                               insertArticlesThread.start();
                           }

                       }
                   }

                   @Override
                   public void onFailure(Call<NewsResponse> call, Throwable t) {

                       RetrieveNewsBySourcesIdThread retrieveNewsBySourcesIdThread =new RetrieveNewsBySourcesIdThread(sourceId, onNewsPreparedListener);
                       retrieveNewsBySourcesIdThread.start();
                   }
               });

   }

   public  interface onSourcesPreparedListener{

       void onSourcesPrepared(List<SourcesItem>sources);
    }

    public  interface onNewsPreparedListener {

        void onNewsPrepared(List<ArticlesItem>newsList);
    }

    class InsertSourcesThread extends Thread{

        List<SourcesItem> sourcesList;
     public InsertSourcesThread(List<SourcesItem>list){
            this.sourcesList=list;
        }
        @Override
        public void run() {

            NewsDataBase.getInstance().sourcesDao().insertSourcesList(sourcesList);
        }
    }

    class RetrieveSourcesThread extends Thread{

        onSourcesPreparedListener onSourcesPreparedListener;
        RetrieveSourcesThread(onSourcesPreparedListener onSourcesPreparedListener1)
        {
            this.onSourcesPreparedListener=onSourcesPreparedListener1;
        }


        @Override
        public void run() {
            List<SourcesItem>sources=NewsDataBase.getInstance().sourcesDao().getAllSources();

            onSourcesPreparedListener.onSourcesPrepared(sources);
        }
    }

    class  InsertArticlesThread extends Thread{

        List<ArticlesItem>newsList;
        public  InsertArticlesThread(List<ArticlesItem>list){
            this.newsList=list;
        }

        @Override
        public void run() {

            for (ArticlesItem item : newsList) {
                item.setSourceId(item.getSource().getId());
                item.setSourceName(item.getSource().getName());
            }
            NewsDataBase.getInstance().newsDao().insertNewsList(newsList);
        }
    }

    class RetrieveNewsBySourcesIdThread extends Thread{

        String sourceId;
        onNewsPreparedListener onNewsPreparedListener1;

      public RetrieveNewsBySourcesIdThread(String sourceId, onNewsPreparedListener onNewsPreparedListener1){
            this.sourceId=sourceId;
            this.onNewsPreparedListener1 = onNewsPreparedListener1;
        }

        @Override
        public void run() {

         List<ArticlesItem>newsList= NewsDataBase.getInstance().newsDao().getNewsBySourceId(sourceId);
         onNewsPreparedListener1.onNewsPrepared(newsList);
        }
    }

}
