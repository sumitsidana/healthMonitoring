#!/usr/bin/octave -qf

clear

listing = dir('training')
listinginfo = dir('distributions')
for i = 3:68
    %     A =  dlmread(strcat('training/',listing(i).name,'/ailments/trainfile'),',')
    %     B =  dlmread(strcat('training/',listing(i).name,'/ailments/testfile'),',')
    C =  dlmread(strcat('training/',listing(i).name,'/topics/trainfile'),',')
    D =  dlmread(strcat('training/',listing(i).name,'/topics/testfile'),',')
    

    %     numRowsAilments = size(A,1)
    numRowsTopics = size(C,1)
    KlDivergenceAilments = zeros(numRowsTopics,1);
    for k = 1:numRowsTopics
        KlDivergenceAilments(k,:) = bhattacharyya(C(k,:),D(k,:))
        %         KlDivergenceAilments(k,:) = KLDiv(C(k,:),D(k,:))
    end
    
    a = ''
    b = strcat('distributions/',listing(i).name,'/topics/')
    space = '';
    filepath1 = char([a space b])
    listingagain = dir(filepath1)
    loop = size(listingagain,1)
    file = fopen(strcat(filepath1,'/','topicshistorical'))
    test = textscan(file,'%s','delimiter','\n','bufsize',50000)
    region = listing(i).name
    listofweeks = cell(length(test{1}),1)
    listweeks = cell(length(test{1}),1)
    for j = 1:length(test{1})
        line = test{1}{j}
        index = strfind(line,'#|#')
        year_week = line(1:index-1)
        index = strfind(year_week,'_')
        %         datetime = strcat(year_week(1:index-1),'.',year_week(index+1:length(year_week)))
        datetime = year_week(index+1:length(year_week))
        listweeks{j} = datetime
        listofweeks{j} = year_week
    end
    
    for k = 1:length(listweeks)
        w = str2double(listweeks(k))
        week(k,:) = w
    end
    
    hold on
    Figure = plot(KlDivergenceAilments);
    NumTicks = length(listofweeks);
    L = get(gca,'XLim');
    set(gca,'XTickLabel',week,'XTick',linspace(L(1),L(2),NumTicks))
    
    saveas(Figure,strcat('distributionbhdifferences/',listing(i).name,'.png'))
    close all
    hold off
    
    clear week
end
