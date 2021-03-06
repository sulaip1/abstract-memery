package com.exelaration.abstractmemery.services.implementations;

import com.exelaration.abstractmemery.domains.Meme;
import com.exelaration.abstractmemery.repositories.MemeRepository;
import com.exelaration.abstractmemery.services.MemeMetadataService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("memeMetadataService")
public class MemeMetadataServiceImpl implements MemeMetadataService {
  @Autowired private MemeRepository memeRepository;

  public List<Meme> findAll() {
    return (List<Meme>) memeRepository.findAll();
  }

  public Optional<Meme> findById(Integer id) {
    return memeRepository.findById(id);
  }

  public Meme save(Meme meme) {
    return memeRepository.save(meme);
  }

  public void deleteById(Integer id) {
    memeRepository.deleteById(id);
  }

  public ArrayList<Meme> getMemes() {
    return memeRepository.findTop10ByIsPublicTrueOrderByIdDesc();
  }

  public ArrayList<Meme> getUserMemes(int userId) {
    return memeRepository.findByUserIdOrderByIdDesc(userId);
  }

  public ArrayList<Meme> findByText(String text) throws Exception {
    if (text.length() > 1) {
      return memeRepository
          .findTop10ByTopTextIgnoreCaseContainsOrBottomTextIgnoreCaseContainsOrMemeNameIgnoreCaseContains(
              text, text, text);
    } else {
      throw new IllegalArgumentException("Search query must contain more than one character");
    }
  }
}
